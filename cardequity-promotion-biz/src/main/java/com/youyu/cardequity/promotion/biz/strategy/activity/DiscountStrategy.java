package com.youyu.cardequity.promotion.biz.strategy.activity;

import com.youyu.cardequity.common.base.annotation.StatusAndStrategyNum;
import com.youyu.cardequity.common.base.converter.BeanPropertiesConverter;
import com.youyu.cardequity.common.base.util.BeanPropertiesUtils;
import com.youyu.cardequity.promotion.biz.dal.dao.ActivityQuotaRuleMapper;
import com.youyu.cardequity.promotion.biz.dal.dao.ActivityRefProductMapper;
import com.youyu.cardequity.promotion.biz.dal.dao.ActivityStageCouponMapper;
import com.youyu.cardequity.promotion.biz.dal.entity.ActivityProfitEntity;
import com.youyu.cardequity.promotion.biz.dal.entity.ActivityQuotaRuleEntity;
import com.youyu.cardequity.promotion.biz.dal.entity.ActivityRefProductEntity;
import com.youyu.cardequity.promotion.biz.dal.entity.ActivityStageCouponEntity;
import com.youyu.cardequity.promotion.biz.utils.CommonUtils;
import com.youyu.cardequity.promotion.dto.*;
import com.youyu.cardequity.promotion.dto.other.ClientCoupStatisticsQuotaDto;
import com.youyu.cardequity.promotion.dto.other.CommonBoolDto;
import com.youyu.cardequity.promotion.dto.other.OrderProductDetailDto;
import com.youyu.cardequity.promotion.enums.CommonDict;
import com.youyu.cardequity.promotion.enums.dict.ApplyProductFlag;
import com.youyu.cardequity.promotion.enums.dict.CouponApplyProductStage;
import com.youyu.cardequity.promotion.enums.dict.TriggerByType;
import com.youyu.cardequity.promotion.vo.domain.QuotaIndexDiffInfo;
import com.youyu.cardequity.promotion.vo.rsp.UseActivityRsp;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by caiyi on 2018/12/26.
 */
@Slf4j
@StatusAndStrategyNum(superClass = ActivityStrategy.class, number = "1", describe = "折扣活动")
@Component
public class DiscountStrategy extends ActivityStrategy {
    @Autowired
    private ActivityRefProductMapper activityRefProductMapper;

    @Autowired
    private ActivityStageCouponMapper activityStageCouponMapper;

    @Autowired
    private ActivityQuotaRuleMapper activityQuotaRuleMapper;

    @Override
    public UseActivityRsp applyActivity(ActivityProfitEntity item, List<OrderProductDetailDto> productList) {
        String discountApplyStage = CouponApplyProductStage.ALL.getDictValue();

        log.info("进入折扣活动处理策略，策略编号为{}", item.getId());
        //应获取于配置开关
        // TODO: 2018/12/26
        //装箱返回数据
        UseActivityRsp rsp = new UseActivityRsp();
        ActivityProfitDto dto = new ActivityProfitDto();
        BeanUtils.copyProperties(item, dto);
        rsp.setActivity(dto);

        //获取活动阶梯
        List<ActivityStageCouponEntity> orgStageList = activityStageCouponMapper.findActivityProfitDetail(item.getId());
        List<ActivityStageCouponEntity> activityProfitDetail = new ArrayList<>();
        //只获取有效的阶梯
        for (ActivityStageCouponEntity stage : orgStageList) {
            if (CommonUtils.isGtZeroDecimal(stage.getProfitValue()) && stage.getProfitValue().compareTo(BigDecimal.ONE) < 0) {
                activityProfitDetail.add(stage);
            }
        }

        //无门槛活动参数校验
        if (activityProfitDetail.size() == 0)
            if (!CommonUtils.isGtZeroDecimal(item.getProfitValue()) || item.getProfitValue().compareTo(BigDecimal.ONE) >= 0)
                return null;

        //2.校验券的额度限制是否满足
        //检查指定客户的额度信息
        ActivityQuotaRuleEntity quota = activityQuotaRuleMapper.findActivityQuotaRuleById(item.getId());
        CommonBoolDto<ClientCoupStatisticsQuotaDto> boolDto = checkActivityPersonQuota(quota, item.getId());
        //校验不通过直接返回
        if (!boolDto.getSuccess()) {
            log.info("客户本人使用额度受限，详情：{}", boolDto.getDesc());
            return null;
        }
        //客户活动优惠统计信息
        ClientCoupStatisticsQuotaDto clientQuotaDto = boolDto.getData();

        //检查所有客户领取额度情况
        boolDto = checkActivityAllQuota(quota);
        //校验不通过直接返回
        if (!boolDto.getSuccess()) {
            log.info("所有客户使用额度受限，详情：{}", boolDto.getDesc());
            return null;
        }
        ClientCoupStatisticsQuotaDto allQuotaDto = boolDto.getData();

        BigDecimal diff = BigDecimal.ZERO;
        BigDecimal applyNum = BigDecimal.ZERO;
        BigDecimal profitCount = BigDecimal.ZERO;
        List<OrderProductDetailDto> temproductLsit = new ArrayList<>();

        //3.有门槛的活动处理
        if (activityProfitDetail.size() > 0) {
            for (ActivityStageCouponEntity stage : activityProfitDetail) {
                //3-1.折扣活动只取优惠力度最大的,同一个活动ProfitValue值越小的阶梯优惠额度越大
                if (rsp.getStage() != null &&
                        rsp.getStage().getProfitValue().compareTo(stage.getProfitValue()) <= 0) {
                    continue;
                }
                BigDecimal countCondition = BigDecimal.ZERO;
                BigDecimal amountCondition = BigDecimal.ZERO;
                temproductLsit.clear();
                //所有活动在定义适用商品时都不会重叠
                for (OrderProductDetailDto productItem : productList) {
                    //1.该商品是否适用于此活动
                    if (!ApplyProductFlag.ALL.getDictValue().equals(item.getApplyProductFlag())) {
                        //该商品属性是否允许参与活动
                        ActivityRefProductEntity entity = activityRefProductMapper.findByActivityAndSkuId(item.getId(), productItem.getProductId(), productItem.getSkuId());
                        if (entity == null) {
                            log.info("该商品不能参与该活动;活动编号：" + item.getId() + ";商品编号" + productItem.getProductId() + ";子商品编号" + productItem.getSkuId());
                            continue;
                        }
                    }
                    //2.后续会多次设置product对象相关值，需要拷贝出来避免污染
                    OrderProductDetailDto product = new OrderProductDetailDto();
                    BeanUtils.copyProperties(productItem, product);
                    applyNum = product.getAppCount();
                    profitCount = product.getAppCount();

                    //3-4.按金额统计门槛
                    if (TriggerByType.CAPITAL.getDictValue().equals(stage.getTriggerByType())) {
                        diff = stage.getBeginValue().subtract(amountCondition);
                        if (CommonUtils.isGtZeroDecimal(diff)) {
                            //满足门槛条件情况下
                            if (product.getTotalAmount().compareTo(diff) >= 0) {
                                //设置达标门槛数量：可能为负数，表示循环上一个商品时就已经满足门槛值了
                                profitCount = diff.divide(product.getPrice(),0, RoundingMode.UP);

                                rsp.setStage(BeanPropertiesUtils.copyProperties(stage, ActivityStageCouponDto.class));
                                log.info("金额门槛折扣活动满足使用条件处理;活动编号：" + item.getId() + ";门槛编号：" + stage.getId() + ";商品编号" + product.getProductId() + ";子商品编号" + product.getSkuId());
                                if (CouponApplyProductStage.CONDITION.getDictValue().equals(discountApplyStage)) {
                                    product.setProfitCount(profitCount);
                                    temproductLsit.add(product);
                                    break;
                                }
                            }
                        }
                    } else {//按数量统计门槛
                        diff = stage.getBeginValue().subtract(countCondition);
                        if (CommonUtils.isGtZeroDecimal(diff)) {
                            //满足门槛条件情况下
                            if (product.getAppCount().subtract(diff).compareTo(BigDecimal.ZERO) >= 0) {
                                profitCount = diff;

                                //所有校验全通关
                                rsp.setStage(BeanPropertiesUtils.copyProperties(stage, ActivityStageCouponDto.class));
                                log.info("数量门槛折扣活动满足使用条件处理;活动编号：" + item.getId() + ";门槛编号：" + stage.getId() + ";商品编号" + product.getProductId() + ";子商品编号" + product.getSkuId());
                                //算入门槛内的才进行打折，否则全部都要打折
                                if (CouponApplyProductStage.CONDITION.getDictValue().equals(discountApplyStage)) {
                                    product.setProfitCount(profitCount);
                                    temproductLsit.add(product);
                                    break;
                                }
                            }
                        }
                    }
                    product.setProfitCount(profitCount);
                    temproductLsit.add(product);
                    countCondition = countCondition.add(product.getAppCount());
                    amountCondition = amountCondition.add(product.getTotalAmount());
                }
                //找到最优惠的折扣
                if (rsp.getStage() != null && rsp.getStage().getId().equals(stage.getId())) {
                    rsp.setProductList(BeanPropertiesConverter.copyPropertiesOfList(temproductLsit, OrderProductDetailDto.class));
                }
            }
        } else {
            //所有活动在定义适用商品时都不会重叠
            for (OrderProductDetailDto productItem : productList) {
                //1.该商品是否适用于此活动
                if (!ApplyProductFlag.ALL.getDictValue().equals(item.getApplyProductFlag())) {
                    //该商品属性是否允许参与活动
                    ActivityRefProductEntity entity = activityRefProductMapper.findByActivityAndSkuId(item.getId(), productItem.getProductId(), productItem.getSkuId());
                    if (entity == null) {
                        log.info("该商品不能参与该活动;活动编号：" + item.getId() + ";商品编号" + productItem.getProductId() + ";子商品编号" + productItem.getSkuId());
                        continue;
                    }
                }
                //2.后续会多次设置product对象相关值，需要拷贝出来避免污染
                OrderProductDetailDto product = new OrderProductDetailDto();
                BeanUtils.copyProperties(productItem, product);

                //优惠门槛数量为0
                product.setProfitCount(BigDecimal.ZERO);
                profitCount = product.getAppCount();
                applyNum = product.getAppCount();

                if (applyNum.compareTo(BigDecimal.ZERO) > 0) {
                    product.setProfitCount(profitCount);
                    rsp.getProductList().add(product);
                    temproductLsit.add(product);
                    log.info("无门槛折扣活动满足使用条件处理;活动编号：" + item.getId() + ";商品编号" + product.getProductId() + ";子商品编号" + product.getSkuId());
                }

            }
        }

        //找到最终适用的阶梯后，将对应最终适用商品情况赋值
        if (!rsp.getProductList().isEmpty()) {
            rsp.setProfitAmount(BigDecimal.ZERO);
            //对于适用商品明细优惠值进行计算
            for (OrderProductDetailDto product : rsp.getProductList()) {
                product.setProfitAmount(product.getTotalAmount().multiply(BigDecimal.ONE.subtract(rsp.getStage() == null ? item.getProfitValue() : rsp.getStage().getProfitValue())));
                rsp.setProfitAmount(rsp.getProfitAmount().add(product.getProfitAmount()));
            }
            return rsp;
        }
        return null;
    }

    /**
     * 计算活动对应商品适用数量，该活动总优惠金额，返回适用的商品范围及数量
     *
     * @param product         选购该商品详情
     * @param applyNum        活动适用数量
     * @param amountCondition 前金额
     * @param stage           适用的阶段
     * @param rsp             需要更新的活动适用总体详情
     * @param temproductLsit  需要更新的适用商品列表
     * @return 订单商品详情
     */
    private List<OrderProductDetailDto> calculationProfitAmount(OrderProductDetailDto product,
                                                                BigDecimal applyNum,
                                                                BigDecimal amountCondition,
                                                                ActivityStageCouponEntity stage,
                                                                UseActivityRsp rsp,
                                                                List<OrderProductDetailDto> temproductLsit) {

        OrderProductDetailDto cyproduct = BeanPropertiesUtils.copyProperties(product, OrderProductDetailDto.class);
        //涉及适用的数量
        cyproduct.setProfitCount(applyNum);

        //商品优惠金额=涉及适用金额*(1-折扣)
        cyproduct.setProfitAmount((cyproduct.getPrice().multiply(applyNum)).multiply(BigDecimal.ONE.subtract(stage.getProfitValue())));
        //总优惠金额=涉及适用的总金额-指定限制的总额
        BigDecimal profitAmount = amountCondition.add(cyproduct.getPrice().multiply(applyNum)).multiply(BigDecimal.ONE.subtract(stage.getProfitValue()));
        rsp.setProfitAmount(profitAmount);

        //最新满足门槛的
        if (rsp.getStage() == null ||
                (stage != null && !rsp.getStage().getId().equals(stage.getId()))) {
            ActivityStageCouponDto stageDto = new ActivityStageCouponDto();
            BeanUtils.copyProperties(stage, stageDto);
            rsp.setStage(stageDto);
            //用最新适用的折扣重算商品对应优惠金额:之前已经满足条件的按全部数量计算优惠金额
            for (OrderProductDetailDto item : rsp.getProductList()) {
                item.setProfitAmount(item.getTotalAmount().multiply(BigDecimal.ONE.subtract(stage.getProfitValue())));
                temproductLsit.add(item);
            }
        }
        temproductLsit.add(cyproduct);

        return temproductLsit;
    }

    /**
     * 获取额度限制下最终允许参与活动数量
     *
     * @param quota           额度限制定义
     * @param clientQuotaDto  客户参加活动统计情况
     * @param allQuotaDto     所有人参加活动统计情况
     * @param product         指定选购商品详情：含数量价格
     * @param countCondition  达到该数量条件，此活动才生效
     * @param amountCondition 达到该买入金额的条件，此活动才生效，计算时需要转换为对应优惠金额和限额比较
     * @param applyNum        适用数量初始值：需校验的值
     * @param discount        折扣值
     * @return 适用数量
     */
    private BigDecimal GetFinalEnableQuota(ActivityQuotaRuleEntity quota,
                                           ClientCoupStatisticsQuotaDto clientQuotaDto,
                                           ClientCoupStatisticsQuotaDto allQuotaDto,
                                           OrderProductDetailDto product,
                                           BigDecimal countCondition,
                                           BigDecimal amountCondition,
                                           BigDecimal applyNum,
                                           BigDecimal discount) {
        //校验限额
        if (quota != null) {
            //单位优惠金额
            BigDecimal profitPerAmount = product.getPrice().multiply(BigDecimal.ONE.subtract(discount));//本商品单价优惠值
            //满足优惠活动条件的优惠金额
            BigDecimal profitConditionAmount = amountCondition.add(profitPerAmount.multiply(product.getProfitCount())).multiply(BigDecimal.ONE.subtract(discount));
            //申请的总优惠金额
            BigDecimal profitApplyAmount = amountCondition.add(product.getPrice().multiply(applyNum)).multiply(BigDecimal.ONE.subtract(discount));

            //满足优惠活动条件的数量
            BigDecimal profitConditionCount = countCondition.add(product.getProfitCount());
            //申请的总优惠金额
            BigDecimal profitApplyCount = countCondition.add(applyNum);

            //1.校验【每笔限额】数量
            applyNum = CommonUtils.GetEnableUseQuota(profitConditionCount, profitApplyCount, quota.getPerMaxCount());
            if (applyNum.compareTo(countCondition.add(BigDecimal.ZERO)) > 0) {
                //校验个人优惠总额
                BigDecimal enableProfitAmount = CommonUtils.GetEnableUseQuota(profitConditionAmount, profitApplyAmount, quota.getPerMaxAmount());
                applyNum = enableProfitAmount.divide(profitPerAmount,0, RoundingMode.DOWN);//重量类商品也是按1单位数量参与活动
                if (applyNum.compareTo(BigDecimal.ZERO) > 0) {
                    //2.校验【个人限额】
                    QuotaIndexDiffInfo personDiffInfo = statisticsQuotaIndexMinDiff(quota, clientQuotaDto);
                    //限额<门槛数量
                    applyNum = CommonUtils.GetEnableUseQuota(profitConditionCount, profitApplyCount, personDiffInfo.getClientDiffCount());
                    if (applyNum.compareTo(BigDecimal.ZERO) > 0) {
                        //先转换为优惠金额比较限额
                        enableProfitAmount = CommonUtils.GetEnableUseQuota(profitConditionAmount, profitApplyAmount, personDiffInfo.getClientDiffAmount());
                        applyNum = enableProfitAmount.divide(profitPerAmount,0, RoundingMode.DOWN);//重量类商品也是按1单位数量参与活动

                        if (applyNum.compareTo(BigDecimal.ZERO) > 0) {
                            //3.校验【全局限额】
                            QuotaIndexDiffInfo allDiffInfo = statisticsQuotaIndexMinDiff(quota, allQuotaDto);
                            applyNum = CommonUtils.GetEnableUseQuota(profitConditionCount, profitApplyCount, allDiffInfo.getClientDiffCount());
                            if (applyNum.compareTo(BigDecimal.ZERO) > 0) {
                                enableProfitAmount = CommonUtils.GetEnableUseQuota(profitConditionAmount, profitApplyAmount, allDiffInfo.getClientDiffAmount());
                                applyNum = enableProfitAmount.divide(profitPerAmount,0, RoundingMode.DOWN);//重量类商品也是按1单位数量参与活动
                            }
                        }
                    }
                }
            }
        }
        return applyNum;
    }
}
