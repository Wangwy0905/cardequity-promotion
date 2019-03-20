package com.youyu.cardequity.promotion.biz.strategy.activity;

import com.youyu.cardequity.common.base.annotation.StatusAndStrategyNum;
import com.youyu.cardequity.promotion.biz.dal.dao.ActivityQuotaRuleMapper;
import com.youyu.cardequity.promotion.biz.dal.dao.ActivityRefProductMapper;
import com.youyu.cardequity.promotion.biz.dal.entity.ActivityProfitEntity;
import com.youyu.cardequity.promotion.biz.dal.entity.ActivityQuotaRuleEntity;
import com.youyu.cardequity.promotion.biz.dal.entity.ActivityRefProductEntity;
import com.youyu.cardequity.promotion.biz.utils.CommonUtils;
import com.youyu.cardequity.promotion.dto.ActivityProfitDto;
import com.youyu.cardequity.promotion.dto.other.ClientCoupStatisticsQuotaDto;
import com.youyu.cardequity.promotion.dto.other.CommonBoolDto;
import com.youyu.cardequity.promotion.dto.other.OrderProductDetailDto;
import com.youyu.cardequity.promotion.enums.CommonDict;
import com.youyu.cardequity.promotion.enums.dict.ApplyProductFlag;
import com.youyu.cardequity.promotion.vo.domain.QuotaIndexDiffInfo;
import com.youyu.cardequity.promotion.vo.rsp.UseActivityRsp;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

/**
 * Created by caiyi on 2018/12/27.
 */
@Slf4j
@StatusAndStrategyNum(superClass = ActivityStrategy.class, number = "2", describe = "优惠价")
@Component
public class PriceStrategy extends ActivityStrategy {

    @Autowired
    private ActivityRefProductMapper activityRefProductMapper;

    @Autowired
    private ActivityQuotaRuleMapper activityQuotaRuleMapper;

    @Override
    public UseActivityRsp applyActivity(ActivityProfitEntity item, List<OrderProductDetailDto> productList) {

        log.info("进入特价活动处理策略，策略编号为{}", item.getId());
        //1.装箱返回数据
        UseActivityRsp rsp = new UseActivityRsp();
        ActivityProfitDto dto = new ActivityProfitDto();
        BeanUtils.copyProperties(item, dto);
        rsp.setActivity(dto);

        //如果特价价格小于等于0直接返回空
        if (!CommonUtils.isGtZeroDecimal(item.getProfitValue())) {
            log.info("特价策略失效，该特价活动特价值必须大于0，活动编号{}", item.getId());
            return null;
        }

        //2.***************校验活动的额度限制是否满足********************
        //检查指定客户的额度信息
        ActivityQuotaRuleEntity quota = activityQuotaRuleMapper.findActivityQuotaRuleById(item.getId());
        CommonBoolDto<ClientCoupStatisticsQuotaDto> boolDto = checkActivityPersonQuota(quota, item.getId());
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
        BigDecimal maxProfitQuotaAmount = alldiffInfo.getMinDiffAmount().min(clientdiffInfo.getMinDiffAmount());
        BigDecimal maxProfitQuotaCount = alldiffInfo.getMinDiffCount().min(clientdiffInfo.getMinDiffCount());


        //3.*****************校验活动门槛*******************
        //所有活动在定义适用商品时都不会重叠
        for (OrderProductDetailDto productItem : productList) {
            //1.该商品是否适用于此活动
            if (!ApplyProductFlag.ALL.getDictValue().equals(item.getApplyProductFlag())) {
                ActivityRefProductEntity entity = activityRefProductMapper.findByActivityAndSkuId(item.getId(), productItem.getProductId(), productItem.getSkuId());
                if (entity == null) {
                    continue;
                }
            }
            //2.参数保护：后续会多次变动product对象相关值，需要拷贝出来避免污染
            OrderProductDetailDto product = new OrderProductDetailDto();
            BeanUtils.copyProperties(productItem, product);
            //总额做保护
            product.setTotalAmount(product.getAppCount().multiply(product.getPrice()));

            //3.初始认定所购买数量都可参与特价活动
            BigDecimal applyNum =  product.getAppCount();
            //特价必须比原价小才起作用
            if (product.getPrice().compareTo(item.getProfitValue()) > 0) {
                //数量额度还存在
                if (maxProfitQuotaCount.compareTo(BigDecimal.ZERO) > 0) {
                    applyNum = applyNum.min(maxProfitQuotaCount);
                }
                //优惠金额额度还存在
                BigDecimal totalProfitAmount = applyNum.multiply(product.getPrice().subtract(item.getProfitValue()));
                if (maxProfitQuotaAmount.compareTo(BigDecimal.ZERO) > 0) {
                    totalProfitAmount = totalProfitAmount.min(maxProfitQuotaAmount);
                    applyNum = totalProfitAmount.divide(product.getPrice().subtract(item.getProfitValue()),0, RoundingMode.DOWN);
                    totalProfitAmount=applyNum.multiply(product.getPrice().subtract(item.getProfitValue()));
                }

                if (applyNum.compareTo(BigDecimal.ZERO) > 0) {
                    maxProfitQuotaCount=maxProfitQuotaCount.subtract(applyNum);
                    maxProfitQuotaAmount=maxProfitQuotaAmount.subtract(totalProfitAmount);

                    product.setProfitAmount(totalProfitAmount);
                    product.setProfitCount(applyNum);
                    rsp.setProfitAmount(rsp.getProfitAmount().add(product.getProfitAmount()));
                    //记录活动适用的商品
                    rsp.getProductList().add(product);
                    log.info("特价活动满足使用条件处理;活动编号：" + item.getId() + ";商品编号" + product.getProductId() + ";子商品编号" + product.getSkuId());
                    //本次优惠已经没有完全满足
                    //if (applyNum.compareTo(product.getAppCount()) < 0) {
                    //    break;
                    //}
                }
            }

        }
        if (rsp.getProductList() == null || rsp.getProductList().isEmpty())
            return null;
        return rsp;
    }


}
