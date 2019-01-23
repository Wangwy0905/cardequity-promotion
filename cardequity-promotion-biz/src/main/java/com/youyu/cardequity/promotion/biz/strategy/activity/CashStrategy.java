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
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 * Created by caiyi on 2018/12/26.
 */
@Slf4j
@StatusAndStrategyNum(superClass = ActivityStrategy.class, number = "3", describe = "现金立减")
@Component
public class CashStrategy extends ActivityStrategy {
    @Autowired
    ActivityRefProductMapper activityRefProductMapper;

    @Autowired
    ActivityStageCouponMapper activityStageCouponMapper;

    @Autowired
    ActivityQuotaRuleMapper activityQuotaRuleMapper;

    @Override
    public UseActivityRsp applyActivity(ActivityProfitEntity item, List<OrderProductDetailDto> productList) {

        log.info("进入普通满减处理策略，策略编号为{}", item.getId());
        //装箱返回数据
        UseActivityRsp rsp = new UseActivityRsp();
        ActivityProfitDto dto = new ActivityProfitDto();
        BeanUtils.copyProperties(item, dto);
        rsp.setActivity(dto);
        rsp.setProfitAmount(item.getProfitValue());
        //默认策略：折扣优惠值是平摊订单涉及到的券定义时适用范围内所有商品
        String applyStage = CouponApplyProductStage.ALL.getDictValue();
        //默认策略：无门槛活动适用范围内仅为第一个商品
        String applyNotStage = CouponApplyProductStage.CONDITION.getDictValue();
        // TODO: 2018/12/27 应取自配置项

        //获取活动阶梯
        List<ActivityStageCouponEntity> activityProfitDetail = activityStageCouponMapper.findActivityProfitDetail(item.getId());
        activityProfitDetail.sort(new Comparator<ActivityStageCouponEntity>() {
            @Override
            public int compare(ActivityStageCouponEntity entity1, ActivityStageCouponEntity entity2) {//如果是折扣、任选、优惠价从小到大
                return entity1.getProfitValue().compareTo(entity2.getProfitValue());
            }
        });

        BigDecimal diff = BigDecimal.ZERO;
        //满足门槛的数量
        BigDecimal applyNum = BigDecimal.ZERO;
        List<OrderProductDetailDto> temproductLsit = new ArrayList<>();

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
        QuotaIndexDiffInfo clientdiffInfo = statisticsQuotaIndexMinDiff(quota, clientQuotaDto);
        QuotaIndexDiffInfo alldiffInfo = statisticsQuotaIndexMinDiff(quota, allQuotaDto);


        //有门槛的活动
        if (activityProfitDetail.size() > 0) {
            //3.有门槛的活动处理
            for (ActivityStageCouponEntity stage : activityProfitDetail) {
                //3-1.折扣活动只取优惠力度最大的
                if (rsp.getStage() != null &&
                        rsp.getStage().getProfitValue().compareTo(stage.getProfitValue()) >= 0)
                    continue;
                BigDecimal countCondition = BigDecimal.ZERO;
                BigDecimal amountCondition = BigDecimal.ZERO;
                //任选活动必须设置有效限额，限额设置为0的情况丢弃
                if (BigDecimal.ZERO.compareTo(stage.getProfitValue()) >= 0)
                    continue;

                BigDecimal maxProfitQuotaAmount = alldiffInfo.getMinDiffAmount().min(clientdiffInfo.getMinDiffAmount());
                BigDecimal maxProfitQuotaCount = alldiffInfo.getMinDiffCount().min(clientdiffInfo.getMinDiffCount());
                if (!TriggerByType.CAPITAL.getDictValue().equals(stage.getTriggerByType())) {
                    //数量额度不够
                    if (maxProfitQuotaCount.compareTo(stage.getBeginValue()) < 0) {
                        continue;
                    }
                }
                //优惠额度不够
                if (maxProfitQuotaAmount.compareTo(stage.getProfitValue()) < 0) {
                    continue;
                }

                temproductLsit.clear();
                //所有活动在定义适用商品时都不会重叠
                for (OrderProductDetailDto productItem : productList) {
                    //1.该商品是否适用于此活动
                    if (!ApplyProductFlag.ALL.getDictValue().equals(item.getApplyProductFlag())) {
                        //该商品属性是否允许参与活动
                        ActivityRefProductEntity entity = activityRefProductMapper.findByActivityAndSkuId(item.getId(), productItem.getProductId(), productItem.getSkuId());
                        if (entity == null) {
                            continue;
                        }
                    }

                    //2.后续会多次设置product对象相关值，需要拷贝出来避免污染
                    OrderProductDetailDto product = new OrderProductDetailDto();
                    BeanUtils.copyProperties(productItem, product);
                    //总额做保护
                    product.setTotalAmount(product.getAppCount().multiply(product.getPrice()));
                    applyNum = product.getAppCount();

                    //3-3.按金额统计门槛
                    if (TriggerByType.CAPITAL.getDictValue().equals(stage.getTriggerByType())) {
                        diff = stage.getBeginValue().subtract(amountCondition);
                        //“第一次”满足门槛条件情况：
                        if (CommonUtils.isGtZeroDecimal(diff)) {
                            if (product.getTotalAmount().compareTo(diff) >= 0) {
                                //满足门槛范围点=向上取整(门槛差额/价格)
                                applyNum = diff.divide(product.getPrice()).setScale(0, BigDecimal.ROUND_UP);
                                //数量额度不足
                                if (maxProfitQuotaCount.compareTo(applyNum) < 0) {
                                    break;
                                }

                                rsp.setStage(BeanPropertiesUtils.copyProperties(stage, ActivityStageCouponDto.class));
                                log.info("金额门槛现金立减券满足使用条件处理;活动编号：" + item.getId() + ";门槛编号：" + stage.getId() + ";商品编号" + product.getProductId() + ";子商品编号" + product.getSkuId());
                                if (CouponApplyProductStage.CONDITION.getDictValue().equals(applyStage)) {
                                    product.setProfitCount(applyNum);
                                    temproductLsit.add(product);
                                    break;
                                }

                            }
                        } else {
                            applyNum = BigDecimal.ZERO;
                        }
                    } else {//按数量统计门槛
                        diff = stage.getBeginValue().subtract(countCondition);
                        //满足门槛条件情况下：将原适用详情temproductLsit替换为最新满足的活动的
                        if (CommonUtils.isGtZeroDecimal(diff)) {
                            //第一次满足条件
                            if (product.getAppCount().subtract(diff).compareTo(BigDecimal.ZERO) >= 0) {
                                applyNum = diff;

                                //得到当前适用的阶梯
                                rsp.setStage(BeanPropertiesUtils.copyProperties(stage, ActivityStageCouponDto.class));
                                log.info("数量门槛现金立减券满足使用条件处理;活动编号：" + item.getId() + ";门槛编号：" + stage.getId() + ";商品编号" + product.getProductId() + ";子商品编号" + product.getSkuId());
                                if (CouponApplyProductStage.CONDITION.getDictValue().equals(applyStage)) {
                                    product.setProfitCount(applyNum);
                                    temproductLsit.add(product);
                                    break;
                                }
                            }
                        } else {
                            applyNum = BigDecimal.ZERO;
                        }

                    }

                    maxProfitQuotaCount = maxProfitQuotaCount.subtract(applyNum);
                    //记录活动适用的商品，但是没有计算对应优惠值，对应优惠值是在满足门槛后再calculationProfitAmount中计算
                    product.setProfitCount(applyNum);
                    temproductLsit.add(product);
                    countCondition = countCondition.add(product.getAppCount());
                    amountCondition = amountCondition.add(product.getTotalAmount());
                }
                //如果满足条件需要把对应商品拷贝替换后组装返回
                if (rsp.getStage() != null && rsp.getStage().getId().equals(stage.getId())) {
                    rsp.setProfitAmount(stage.getProfitValue());
                    rsp.setProductLsit(BeanPropertiesConverter.copyPropertiesOfList(temproductLsit, OrderProductDetailDto.class));
                }
            }
        } else {
            BigDecimal maxProfitQuotaAmount = alldiffInfo.getMinDiffAmount().min(clientdiffInfo.getMinDiffAmount());
            BigDecimal maxProfitQuotaCount = alldiffInfo.getMinDiffCount().min(clientdiffInfo.getMinDiffCount());

            //优惠额度不够
            if (maxProfitQuotaAmount.compareTo(item.getProfitValue()) < 0) {
                return null;
            }
            //所有活动在定义适用商品时都不会重叠
            for (OrderProductDetailDto productItem : productList) {
                //1.该商品是否适用于此活动
                if (!ApplyProductFlag.ALL.getDictValue().equals(item.getApplyProductFlag())) {
                    //该商品属性是否允许参与活动
                    ActivityRefProductEntity entity = activityRefProductMapper.findByActivityAndSkuId(item.getId(), productItem.getProductId(), productItem.getSkuId());
                    if (entity == null) {
                        continue;
                    }
                }
                //2.后续会多次设置product对象相关值，需要拷贝出来避免污染
                OrderProductDetailDto product = new OrderProductDetailDto();
                BeanUtils.copyProperties(productItem, product);
                //总额做保护
                product.setTotalAmount(product.getAppCount().multiply(product.getPrice()));
                applyNum = product.getAppCount();
                //数量额度还存在
                if (maxProfitQuotaCount.compareTo(BigDecimal.ZERO) <= 0) {
                    break;
                }
                applyNum = applyNum.min(maxProfitQuotaCount);

                //优惠金额额度还存在
                if (maxProfitQuotaAmount.compareTo(BigDecimal.ZERO) <= 0) {
                    break;
                }
                BigDecimal totalProfitAmount = item.getProfitValue();
                if (CouponApplyProductStage.CONDITION.getDictValue().equals(applyNotStage)) {
                    applyNum = BigDecimal.ONE;
                } else {
                    totalProfitAmount = applyNum.multiply(item.getProfitValue());
                    if (maxProfitQuotaAmount.compareTo(BigDecimal.ZERO) > 0) {
                        totalProfitAmount = totalProfitAmount.min(maxProfitQuotaAmount);
                        applyNum = totalProfitAmount.divide(item.getProfitValue()).setScale(0, BigDecimal.ROUND_DOWN);
                        totalProfitAmount = applyNum.multiply(item.getProfitValue());
                    }
                }

                if (applyNum.compareTo(BigDecimal.ZERO) > 0) {
                    rsp.setProfitAmount(applyNum.multiply(item.getProfitValue()));
                    rsp.getProductLsit().add(product);
                    product.setProfitCount(applyNum);
                    log.info("无门槛现金立减活动满足使用条件;活动编号" + item.getId() + ";商品编号" + product.getProductId() + ";子商品编号" + product.getSkuId());
                    if (CouponApplyProductStage.CONDITION.getDictValue().equals(applyNotStage)) {
                        break;
                    }
                }
                maxProfitQuotaCount = maxProfitQuotaCount.subtract(applyNum);
                maxProfitQuotaAmount = maxProfitQuotaAmount.subtract(totalProfitAmount);
            }
        }


        //1.有门槛、满足门槛；2.无门槛、有满足的商品、模式为适用一个数量的活动
        if ((!activityProfitDetail.isEmpty() && rsp.getStage() != null) ||
                (activityProfitDetail.isEmpty() && !rsp.getProductLsit().isEmpty() && CouponApplyProductStage.CONDITION.getDictValue().equals(applyNotStage))) {
            BigDecimal totalRealAmount = rsp.getProductLsit().stream().map(OrderProductDetailDto::getTotalAmount).reduce(BigDecimal.ZERO, BigDecimal::add);
            //每种商品优惠的金额是按适用金额比例来的
            if (totalRealAmount.compareTo(BigDecimal.ZERO) > 0) {
                for (OrderProductDetailDto product : rsp.getProductLsit()) {
                    product.setProfitAmount(rsp.getProfitAmount().multiply(product.getTotalAmount().divide(totalRealAmount)));
                }
            }
            return rsp;
        } else {
            //无门槛、有满足的商品、模式为适用所有数量模式
            if (activityProfitDetail.isEmpty() && !rsp.getProductLsit().isEmpty() && CouponApplyProductStage.CONDITION.getDictValue().equals(applyNotStage)) {
                BigDecimal totalProfitAmount = BigDecimal.ZERO;
                //无门槛的每个商品都是单独优惠金额
                for (OrderProductDetailDto product : rsp.getProductLsit()) {
                    totalProfitAmount = totalProfitAmount.add(item.getProfitValue().multiply(product.getProfitCount()));
                    product.setProfitAmount(item.getProfitValue().multiply(product.getProfitCount()));
                }
                rsp.setProfitAmount(totalProfitAmount);
                return rsp;
            }
        }

        return null;
    }

}
