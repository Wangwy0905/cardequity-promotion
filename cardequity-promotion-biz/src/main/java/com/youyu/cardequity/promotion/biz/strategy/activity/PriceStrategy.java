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
import com.youyu.cardequity.promotion.enums.dict.ApplyProductFlag;
import com.youyu.cardequity.promotion.vo.domain.QuotaIndexDiffInfo;
import com.youyu.cardequity.promotion.vo.rsp.UseActivityRsp;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
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

        log.info("进入特价活动处理策略，策略编号为{}",item.getId());
        //装箱返回数据
        UseActivityRsp rsp = new UseActivityRsp();
        ActivityProfitDto dto = new ActivityProfitDto();
        BeanUtils.copyProperties(item, dto);
        rsp.setActivity(dto);
        rsp.setProfitAmount(item.getProfitValue());

        if (!CommonUtils.isGtZeroDecimal(item.getProfitValue())) {
            return null;
        }

        //2.校验券的额度限制是否满足
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

        BigDecimal countCondition=BigDecimal.ZERO;
        BigDecimal applyNum=BigDecimal.ZERO;
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

            applyNum=product.getAppCount();
            //特价必须比原价小才起作用
            if (product.getPrice().compareTo(item.getProfitValue()) > 0) {
                applyNum= GetFinalEnableQuota( quota,
                         clientQuotaDto,
                         allQuotaDto,
                         product,
                         countCondition,
                         applyNum,
                        rsp.getProfitAmount(),
                        item.getProfitValue());
                if (applyNum.compareTo(BigDecimal.ZERO)>0) {
                    product.setProfitAmount(applyNum.multiply(product.getPrice().subtract(item.getProfitValue())));
                    product.setProfitCount(applyNum);
                    rsp.setProfitAmount(rsp.getProfitAmount().add(product.getProfitAmount()));
                    //记录活动适用的商品
                    rsp.getProductLsit().add(product);
                    countCondition = countCondition.add(product.getAppCount());
                    log.info("特价活动满足使用条件处理;活动编号：" + item.getId() + ";商品编号" + product.getProductId() + ";子商品编号" + product.getSkuId());

                }
            }
        }
        return rsp;
    }


    /**
     * 获取额度限制下最终允许参与活动数量
     *
     * @param quota          额度限制定义
     * @param clientQuotaDto 客户参加活动统计情况
     * @param allQuotaDto    所有人参加活动统计情况
     * @param product        指定选购商品详情：含数量价格
     * @param countCondition 已达到条件商品数量，用于和本次累加计算数量是否超限
     * @param applyNum       适用数量初始值：需校验的值
     * @param profitAmount   单个商品优惠价
     * @return
     */
    private BigDecimal GetFinalEnableQuota(ActivityQuotaRuleEntity quota,
                                           ClientCoupStatisticsQuotaDto clientQuotaDto,
                                           ClientCoupStatisticsQuotaDto allQuotaDto,
                                           OrderProductDetailDto product,
                                           BigDecimal countCondition,
                                           BigDecimal applyNum,
                                           BigDecimal preProfitAmount,
                                           BigDecimal profitAmount) {
        //校验限额
        if (quota != null) {
            //单个商品的优惠金额
            BigDecimal profitPerAmount = product.getPrice().subtract(profitAmount);//本商品单价优惠值
            //满足优惠活动条件的数量
            BigDecimal profitConditionCount = countCondition.add(product.getProfitCount());
            if (profitPerAmount.compareTo(BigDecimal.ZERO)<=0)
                return BigDecimal.ZERO;

            //1.校验【每笔限额】数量
            applyNum = CommonUtils.GetEnableUseQuota(profitConditionCount, applyNum, quota.getPerMaxCount());
            if (applyNum.compareTo(BigDecimal.ZERO) > 0) {

                //满足优惠活动条件的优惠金额：已优惠金额+本商品起码满足一个的优惠金额
                BigDecimal profitConditionAmount = preProfitAmount.add(profitPerAmount);
                //想要全部优惠的优惠金额
                BigDecimal profitApplyAmount = preProfitAmount.add(profitPerAmount.multiply(applyNum));
                //最终能优惠的金额
                BigDecimal enableProfitAmount = CommonUtils.GetEnableUseQuota(profitConditionAmount, profitApplyAmount, quota.getPerMaxAmount());
                //最终能优惠的金额转换为可优惠的数量
                applyNum = enableProfitAmount.divide(profitPerAmount).setScale(0, BigDecimal.ROUND_DOWN);//重量类商品也是按1单位数量参与活动

                if (applyNum.compareTo(BigDecimal.ZERO) > 0) {
                    //2.校验【个人限额】
                    QuotaIndexDiffInfo personDiffInfo = statisticsQuotaIndexMinDiff(quota, clientQuotaDto);
                    //限额<门槛数量
                    applyNum = CommonUtils.GetEnableUseQuota(profitConditionCount, applyNum, personDiffInfo.getClientDiffCount());
                    if (applyNum.compareTo(BigDecimal.ZERO) > 0) {

                        enableProfitAmount = CommonUtils.GetEnableUseQuota(profitConditionAmount, profitApplyAmount, personDiffInfo.getClientDiffAmount());
                        applyNum = enableProfitAmount.divide(profitPerAmount).setScale(0, BigDecimal.ROUND_DOWN);//重量类商品也是按1单位数量参与活动

                        if (applyNum.compareTo(BigDecimal.ZERO) > 0) {
                            //3.校验【全局限额】
                            QuotaIndexDiffInfo allDiffInfo = statisticsQuotaIndexMinDiff(quota, allQuotaDto);
                            applyNum = CommonUtils.GetEnableUseQuota(profitConditionCount, applyNum, allDiffInfo.getClientDiffCount());
                            if (applyNum.compareTo(BigDecimal.ZERO) > 0) {
                                enableProfitAmount = CommonUtils.GetEnableUseQuota(profitConditionAmount, profitApplyAmount, allDiffInfo.getClientDiffAmount());
                                applyNum = enableProfitAmount.divide(profitPerAmount).setScale(0, BigDecimal.ROUND_DOWN);//重量类商品也是按1单位数量参与活动
                            }
                        }
                    }
                }
            }
        }
        return applyNum;
    }
}
