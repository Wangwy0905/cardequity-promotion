package com.youyu.cardequity.promotion.biz.strategy.activity;

import com.youyu.cardequity.common.base.annotation.StatusAndStrategyNum;
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

        //装箱返回数据
        UseActivityRsp rsp = new UseActivityRsp();
        ActivityProfitDto dto = new ActivityProfitDto();
        BeanUtils.copyProperties(item, dto);
        rsp.setActivity(dto);
        rsp.setProfitAmount(item.getProfitValue());
        //默认策略：折扣优惠值是平摊订单涉及到的券定义时适用范围内所有商品
        String applyStage = CouponApplyProductStage.ALL.getDictValue();
        // TODO: 2018/12/27 应取自配置项

        //获取活动阶梯
        List<ActivityStageCouponEntity> activityProfitDetail = activityStageCouponMapper.findActivityProfitDetail(item.getId());

        activityProfitDetail.sort(new Comparator<ActivityStageCouponEntity>() {
            @Override
            public int compare(ActivityStageCouponEntity entity1, ActivityStageCouponEntity entity2) {//如果是折扣、任选、优惠价从小到大
                return entity1.getProfitValue().compareTo(entity2.getProfitValue());
            }
        });


        BigDecimal countCondition = BigDecimal.ZERO;
        BigDecimal amountCondition = BigDecimal.ZERO;
        BigDecimal diff = BigDecimal.ZERO;
        BigDecimal applyNum = BigDecimal.ZERO;
        List<OrderProductDetailDto> temproductLsit = new ArrayList<>();

        //2.校验券的额度限制是否满足
        //检查指定客户的额度信息
        ActivityQuotaRuleEntity quota = activityQuotaRuleMapper.findActivityQuotaRuleById(item.getId());
        CommonBoolDto<ClientCoupStatisticsQuotaDto> boolDto = checkActivityPersonQuota(quota, item.getId());
        //校验不通过直接返回
        if (!boolDto.getSuccess()) {
            return null;
        }
        //客户活动优惠统计信息
        ClientCoupStatisticsQuotaDto clientQuotaDto = boolDto.getData();

        //检查所有客户领取额度情况
        boolDto = checkActivityAllQuota(quota);
        //校验不通过直接返回
        if (!boolDto.getSuccess()) {
            return null;
        }
        ClientCoupStatisticsQuotaDto allQuotaDto = boolDto.getData();


        //所有活动在定义适用商品时都不会重叠
        for (OrderProductDetailDto productItem : productList) {
            //1.该商品是否适用于此活动
            if (!ApplyProductFlag.ALL.getDictValue().equals(item.getApplyProductFlag())) {
                //该商品属性是否允许参与活动
                ActivityRefProductEntity entity = activityRefProductMapper.findByBothId(item.getId(), productItem.getProductId());
                if (entity == null) {
                    continue;
                }
            }
            //2.后续会多次设置product对象相关值，需要拷贝出来避免污染
            OrderProductDetailDto product = new OrderProductDetailDto();
            BeanUtils.copyProperties(productItem, product);
            //总额做保护
            product.setTotalAmount(product.getAppCount().multiply(product.getPrice()));

            applyNum=product.getAppCount();
            //有门槛的活动
            if (activityProfitDetail.size() > 0) {
                //3.有门槛的活动处理
                for (ActivityStageCouponEntity stage : activityProfitDetail) {
                    //3-1.折扣活动只取优惠力度最大的
                    if (rsp.getStage() != null &&
                            rsp.getStage().getProfitValue().compareTo(stage.getProfitValue()) >= 0)
                        continue;

                    //3-2.满减活动只适用于满足条件的个数和商品，则后面商品都不算入该阶梯适用商品
                    //默认策略：折扣优惠值是平摊订单涉及到的券定义时适用范围内所有商品
                    if (CouponApplyProductStage.CONDITION.getDictValue().equals(applyStage)) {
                        if (rsp.getStage() != null &&
                                rsp.getStage().getId().equals(stage.getId())) {
                            continue;
                        }
                    }

                    //临时变量：在满足门槛时是否适用与额度限制内

                    if (rsp.getStage() != null && rsp.getStage().getId().equals(stage.getId())) {
                        //满足门槛后额度也不需要校验的，因为满减类型的满足条件后优惠值固定和适用商品多少无关，所以相关额度不需要再计算
                        calculationProfitAmount(product, BigDecimal.ZERO, stage, rsp, temproductLsit);
                    } else {
                        //3-3.按金额统计门槛
                        if (TriggerByType.CAPITAL.getDictValue().equals(stage.getTriggerByType())) {
                            diff = stage.getBeginValue().subtract(amountCondition);
                            //满足门槛条件情况下：将原适用详情temproductLsit替换为最新满足的活动的
                            if (product.getTotalAmount().compareTo(diff) >= 0) {
                                //满足门槛范围点=向上取整(门槛差额/价格)，后续循环会被3-2限制住
                                applyNum = diff.divide(product.getPrice()).setScale(0, BigDecimal.ROUND_UP);

                                applyNum = GetFinalEnableQuota(quota,
                                        clientQuotaDto,
                                        allQuotaDto,
                                        product,
                                        countCondition,
                                        applyNum,
                                        stage.getProfitValue());
                                if (applyNum.compareTo(BigDecimal.ZERO) > 0) {
                                    temproductLsit.clear();
                                    //计算：该商品适用优惠金额、该活动总优惠金额、及适用商品和数量
                                    calculationProfitAmount(product, applyNum, stage, rsp, temproductLsit);
                                }
                            }

                        } else {//按数量统计门槛
                            diff = stage.getBeginValue().subtract(countCondition);
                            //满足门槛条件情况下：将原适用详情temproductLsit替换为最新满足的活动的
                            if (product.getAppCount().subtract(diff).compareTo(BigDecimal.ZERO) >= 0) {
                                applyNum = diff;

                                applyNum = GetFinalEnableQuota(quota,
                                        clientQuotaDto,
                                        allQuotaDto,
                                        product,
                                        countCondition,
                                        applyNum,
                                        stage.getProfitValue());
                                if (applyNum.compareTo(BigDecimal.ZERO) > 0) {
                                    temproductLsit.clear();
                                    //计算：该商品适用优惠金额、该活动总优惠金额、及适用商品和数量
                                    calculationProfitAmount(product, applyNum, stage, rsp, temproductLsit);
                                }
                            }

                        }

                    }
                }
                //无门槛的活动
            }else{
                //第一次进来时候进行限额校验
                if (temproductLsit.size()==0) {
                    applyNum = GetFinalEnableQuota(quota,
                            clientQuotaDto,
                            allQuotaDto,
                            product,
                            BigDecimal.ZERO,//无门槛
                            applyNum,
                            item.getProfitValue());
                    if (applyNum.compareTo(BigDecimal.ZERO) <= 0) {
                        return null;
                    }
                }
            }

            //记录活动适用的商品，但是没有计算对应优惠值，对应优惠值是在满足门槛后再calculationProfitAmount中计算
            rsp.getProductLsit().add(product);
            countCondition = countCondition.add(product.getAppCount());
            amountCondition = amountCondition.add(product.getAppCount().multiply(product.getPrice()));
        }

        //找到最终适用的阶梯后，将对应最终适用商品情况赋值
        if (activityProfitDetail.size() > 0) {
            if (rsp.getStage() != null) {
                rsp.setProductLsit(temproductLsit);
                BigDecimal totalRealAmount = rsp.getProductLsit().stream().map(OrderProductDetailDto::getTotalAmount).reduce(BigDecimal.ZERO, BigDecimal::add);
                //每种商品优惠的金额是按适用金额比例来的
                if (totalRealAmount.compareTo(BigDecimal.ZERO) > 0) {
                    for (OrderProductDetailDto product : rsp.getProductLsit()) {
                        product.setProfitAmount(rsp.getProfitAmount().multiply(product.getTotalAmount().divide(totalRealAmount)));
                    }
                }
                return rsp;
            }
            //无适用门槛立减活动需要将每个商品的优惠值进行再次计算；每个数量单独适用该立减
        } else {
            for (OrderProductDetailDto product : rsp.getProductLsit()) {
                product.setProfitAmount(product.getAppCount().multiply(item.getProfitValue()));
            }
            return rsp;
        }
        return null;
    }

    /**
     * 计算活动对应商品适用数量，该活动总优惠金额，返回适用的商品范围及数量
     *
     * @param product  选购商品详情
     * @param applyNum 计算门槛时适用数量
     * @param stage 适用阶梯
     * @param rsp 适用总体详情
     * @param temproductLsit 适用商品明细情况
     * @return
     */
    private List<OrderProductDetailDto> calculationProfitAmount(OrderProductDetailDto product,
                                                                BigDecimal applyNum,
                                                                ActivityStageCouponEntity stage,
                                                                UseActivityRsp rsp,
                                                                List<OrderProductDetailDto> temproductLsit) {

        OrderProductDetailDto cyproduct = new OrderProductDetailDto();
        BeanUtils.copyProperties(product, cyproduct);
        //涉及适用的数量
        cyproduct.setProfitCount(applyNum);

        //优惠金额=涉及适用的总金额-指定限制的总额
        rsp.setProfitAmount(stage.getProfitValue());
        if (rsp.getStage() != null && rsp.getStage().getId().equals(stage.getId())) {
            //得到当前适用的阶梯
            ActivityStageCouponDto stageDto = new ActivityStageCouponDto();
            BeanUtils.copyProperties(stage, stageDto);
            rsp.setStage(stageDto);
        }

        if (temproductLsit==null || temproductLsit.size()<=0) {
            if (temproductLsit==null )
                temproductLsit=new ArrayList<>();
            //适用商品列表=之前已匹配商品+本次达到门槛适用商品及数量
            temproductLsit.addAll(rsp.getProductLsit());
        }
        temproductLsit.add(cyproduct);
        return temproductLsit;
    }

    /**
     * 获取额度限制下最终允许参与活动数量
     *
     * @param quota          额度限制定义
     * @param clientQuotaDto 客户参加活动统计情况
     * @param allQuotaDto    所有人参加活动统计情况
     * @param product        指定选购商品详情：含数量价格
     * @param countCondition 达到该数量条件，此活动才生效
     * @param applyNum       适用数量初始值：需校验的值
     * @param profitAmount 本活动满减值
     * @return
     */
    private BigDecimal GetFinalEnableQuota(ActivityQuotaRuleEntity quota,
                                           ClientCoupStatisticsQuotaDto clientQuotaDto,
                                           ClientCoupStatisticsQuotaDto allQuotaDto,
                                           OrderProductDetailDto product,
                                           BigDecimal countCondition,
                                           BigDecimal applyNum,
                                           BigDecimal profitAmount) {
        //校验限额
        if (quota != null) {
            //满足优惠活动条件的优惠金额
            BigDecimal profitConditionAmount = profitAmount;
            //申请的总优惠金额
            BigDecimal profitApplyAmount = profitAmount;
            //满足优惠活动条件的数量
            BigDecimal profitConditionCount = countCondition.add(product.getProfitCount());
            //1.校验【每笔限额】数量
            applyNum = CommonUtils.GetEnableUseQuota(profitConditionCount, applyNum, quota.getPerMaxCount());
            if (applyNum.compareTo(BigDecimal.ZERO) > 0) {
                //2.校验【每笔优惠总额】：对满减优惠要么不通过，要么全部applyNum数量通过
                BigDecimal enableProfitAmount = CommonUtils.GetEnableUseQuota(profitConditionAmount, profitApplyAmount, quota.getPerMaxAmount());
                if (enableProfitAmount.compareTo(BigDecimal.ZERO) <= 0)
                    applyNum = BigDecimal.ZERO;
                if (applyNum.compareTo(BigDecimal.ZERO) > 0) {
                    //3.校验【个人限额】优惠数量
                    QuotaIndexDiffInfo personDiffInfo = statisticsQuotaIndexMinDiff(quota, clientQuotaDto);
                    //限额<门槛数量
                    applyNum = CommonUtils.GetEnableUseQuota(profitConditionCount, applyNum, personDiffInfo.getClientDiffCount());
                    if (applyNum.compareTo(BigDecimal.ZERO) > 0) {
                        //4.校验【个人限额】优惠金额
                        enableProfitAmount = CommonUtils.GetEnableUseQuota(profitConditionAmount, profitApplyAmount, personDiffInfo.getClientDiffAmount());
                        //校验不通过：对满减优惠要么不通过，要么全部applyNum数量通过
                        if (enableProfitAmount.compareTo(BigDecimal.ZERO) <= 0)
                            applyNum = BigDecimal.ZERO;
                        if (applyNum.compareTo(BigDecimal.ZERO) > 0) {
                            //5.校验【全局限额】
                            QuotaIndexDiffInfo allDiffInfo = statisticsQuotaIndexMinDiff(quota, allQuotaDto);
                            applyNum = CommonUtils.GetEnableUseQuota(profitConditionCount, applyNum, allDiffInfo.getClientDiffCount());
                            if (applyNum.compareTo(countCondition.add(BigDecimal.ZERO)) > 0) {
                                enableProfitAmount = CommonUtils.GetEnableUseQuota(profitConditionAmount, profitApplyAmount, allDiffInfo.getClientDiffAmount());
                                //校验不通过
                                if (enableProfitAmount.compareTo(BigDecimal.ZERO) <= 0)
                                    applyNum = BigDecimal.ZERO;
                            }
                        }
                    }
                }
            }
        }
        return applyNum;
    }
}
