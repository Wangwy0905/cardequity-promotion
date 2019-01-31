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
import com.youyu.cardequity.promotion.enums.CommonDict;
import com.youyu.cardequity.promotion.enums.dict.ApplyProductFlag;
import com.youyu.cardequity.promotion.enums.dict.TriggerByType;
import com.youyu.cardequity.promotion.vo.rsp.UseActivityRsp;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.List;

/**
 * Created by caiyi on 2018/12/26.
 */
@Slf4j
@StatusAndStrategyNum(superClass = ActivityStrategy.class, number = "5", describe = "每满N减M元")
@Component
public class EqualStageCashStrategy  extends ActivityStrategy {

    @Autowired
    private ActivityRefProductMapper activityRefProductMapper;

    @Autowired
    private ActivityStageCouponMapper activityStageCouponMapper;
    @Autowired
    private ActivityQuotaRuleMapper activityQuotaRuleMapper;

    @Override
    public UseActivityRsp applyActivity(ActivityProfitEntity item, List<OrderProductDetailDto> productList) {

        log.info("进入等阶满减活动处理策略，策略编号为{}",item.getId());
        //装箱返回数据
        UseActivityRsp rsp = new UseActivityRsp();
        ActivityProfitDto dto = new ActivityProfitDto();
        BeanUtils.copyProperties(item, dto);
        rsp.setActivity(dto);
        rsp.setProfitAmount(item.getProfitValue());

        //获取活动阶梯
        List<ActivityStageCouponEntity> activityProfitDetail = activityStageCouponMapper.findActivityProfitDetail(item.getId());

        //每满N减M一定要设置门槛，有且只有一个
        if (activityProfitDetail.size() <= 0) {
            return null;
        }
        ActivityStageCouponEntity stage = activityProfitDetail.get(0);
        //活动阶梯步长不能小于等于0
        if (!CommonUtils.isGtZeroDecimal(stage.getBeginValue()))
            return null;
        //封顶值比门槛还低是废数据
        if (CommonUtils.isGtZeroDecimal(stage.getEndValue()) && stage.getEndValue().compareTo(stage.getBeginValue())<0){
            return null;
        }
        //优惠金额不大于0是废数据
        if (!CommonUtils.isGtZeroDecimal(stage.getProfitValue())){
            return null;
        }

        //2.校验券的额度限制是否满足:只校验参与活动前是否有剩余额度，最终额度再和封顶值配合控制
        //检查指定客户的额度信息
        ActivityQuotaRuleEntity quota = activityQuotaRuleMapper.findActivityQuotaRuleById(item.getId());
        CommonBoolDto<ClientCoupStatisticsQuotaDto> boolDto = checkActivityPersonQuota(quota, item.getId());
        //校验不通过直接返回
        if (!boolDto.getSuccess()) {
            log.info("客户本人使用额度受限，详情：{}",boolDto.getDesc());
            return null;
        }
        //客户活动优惠统计信息
        ClientCoupStatisticsQuotaDto clientQuotaDto = boolDto.getData();

        //检查所有客户领取额度情况
        boolDto = checkActivityAllQuota(quota);
        //校验不通过直接返回
        if (!boolDto.getSuccess()) {
            log.info("所有客户使用额度受限，详情：{}",boolDto.getDesc());
            return null;
        }
        ClientCoupStatisticsQuotaDto allQuotaDto = boolDto.getData();

        BigDecimal countCondition = BigDecimal.ZERO;
        BigDecimal amountCondition = BigDecimal.ZERO;
        //所有活动在定义适用商品时都不会重叠
        for (OrderProductDetailDto productItem : productList) {
            //1.该商品是否适用于此活动
            if (!ApplyProductFlag.ALL.getDictValue().equals(item.getApplyProductFlag())) {
                //该商品属性是否允许参与活动
                ActivityRefProductEntity entity = activityRefProductMapper.findByActivityAndSkuId(item.getId(), productItem.getProductId(),productItem.getSkuId());
                if (entity == null) {
                    continue;
                }
            }
            //2.后续会多次设置product对象相关值，需要拷贝出来避免污染
            OrderProductDetailDto product = new OrderProductDetailDto();
            BeanUtils.copyProperties(productItem, product);
            //总额做保护
            product.setTotalAmount(product.getAppCount().multiply(product.getPrice()));

            //记录活动适用的商品，但是没有计算对应优惠值，对应优惠值是在满足门槛后再calculationProfitAmount中计算
            rsp.getProductList().add(product);
            countCondition = countCondition.add(product.getAppCount());
            amountCondition = amountCondition.add(product.getTotalAmount());
        }

        BigDecimal applyNum =BigDecimal.ZERO;
        if (TriggerByType.NUMBER.getDictValue().equals(stage.getTriggerByType())) {
            //没有满足条件
            if (countCondition.compareTo(stage.getBeginValue())<0)
                return null;
            //不能超过封顶值
            if (CommonDict.CONTINUEVALID.getCode().equals(CommonUtils.isQuotaValueNeedValidFlag(stage.getEndValue())) &&
                    stage.getEndValue().compareTo(amountCondition) < 0){
                applyNum = stage.getEndValue().divide(stage.getBeginValue()).setScale(0, BigDecimal.ROUND_DOWN);
            } else {
                applyNum = amountCondition.divide(stage.getBeginValue()).setScale(0, BigDecimal.ROUND_DOWN);
            }
        }else {
            //没有满足条件
            if (amountCondition.compareTo(stage.getBeginValue())<0)
                return null;

            //不能超过封顶值
            if (CommonDict.CONTINUEVALID.getCode().equals(CommonUtils.isQuotaValueNeedValidFlag(stage.getEndValue())) &&
                    stage.getEndValue().compareTo(countCondition) < 0) {
                applyNum = stage.getEndValue().divide(stage.getBeginValue()).setScale(0, BigDecimal.ROUND_DOWN);
            } else {
                applyNum = countCondition.divide(stage.getBeginValue()).setScale(0, BigDecimal.ROUND_DOWN);
            }
        }

        //达到门槛才返回
        if (applyNum.compareTo(BigDecimal.ZERO) > 0) {
            //设置满足条件的门槛信息
            ActivityStageCouponDto stageDto = new ActivityStageCouponDto();
            BeanUtils.copyProperties(stage, stageDto);
            rsp.setStage(stageDto);
            //计算活动总优惠金额=步长倍数*每个步长优惠值
            BigDecimal totalProfitAmount = applyNum.multiply(stage.getProfitValue());
            rsp.setProfitAmount(totalProfitAmount);

            //每种商品优惠的金额是按适用金额比例来的
            if (amountCondition.compareTo(BigDecimal.ZERO) > 0) {
                for (OrderProductDetailDto product : rsp.getProductList()) {
                    product.setProfitAmount(rsp.getProfitAmount().multiply(product.getTotalAmount().divide(amountCondition)));
                }
            }
            log.info("等阶立减券满足使用条件处理;活动编号：" + item.getId() +";门槛编号："+stage.getId());
            return rsp;
        }
        return null;
    }
}
