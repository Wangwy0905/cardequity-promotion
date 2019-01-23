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
        rsp.setProfitAmount(item.getProfitValue());

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

        //3.*****************校验活动门槛*******************
        BigDecimal countCondition = BigDecimal.ZERO;
        BigDecimal applyNum = BigDecimal.ZERO;
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
            applyNum = product.getAppCount();
            //特价必须比原价小才起作用
            if (product.getPrice().compareTo(item.getProfitValue()) > 0) {

                //校验每笔的额度
                CommonBoolDto<BigDecimal> enableQuota = checkPerFinalEnableQuota(quota,
                        BigDecimal.ZERO,
                        CommonDict.IF_YES.getCode(),
                        applyNum,
                        product.getPrice().subtract(item.getProfitValue()));
                applyNum = enableQuota.getData();

                //统计每人的额度
                enableQuota = checkTotalFinalEnableQuota(statisticsQuotaIndexMinDiff(quota, clientQuotaDto),
                        BigDecimal.ZERO,
                        CommonDict.IF_YES.getCode(),
                        applyNum,
                        product.getPrice().subtract(item.getProfitValue()));
                applyNum = enableQuota.getData();

                //统计所有人的额度
                enableQuota = checkTotalFinalEnableQuota(statisticsQuotaIndexMinDiff(quota, clientQuotaDto),
                        BigDecimal.ZERO,
                        CommonDict.IF_YES.getCode(),
                        applyNum,
                        product.getPrice().subtract(item.getProfitValue()));
                applyNum = enableQuota.getData();


            }

            if (applyNum.compareTo(BigDecimal.ZERO) > 0) {
                product.setProfitAmount(applyNum.multiply(product.getPrice().subtract(item.getProfitValue())));
                product.setProfitCount(applyNum);
                rsp.setProfitAmount(rsp.getProfitAmount().add(product.getProfitAmount()));
                //记录活动适用的商品
                rsp.getProductLsit().add(product);
                countCondition = countCondition.add(product.getAppCount());
                log.info("特价活动满足使用条件处理;活动编号：" + item.getId() + ";商品编号" + product.getProductId() + ";子商品编号" + product.getSkuId());

            }

        }
        return rsp;
    }


    /**
     * 获取“按笔”限额最终允许参与活动数量
     *
     * @param quota           额度限制配置
     * @param conditionFlag   0-vauleCondition标识数量 1-标识金额
     * @param vauleCondition  满足优惠活动条件的时最低优惠数量
     * @param applyNum        适用数量初始值：需校验的值
     * @param profitPerAmount 单个商品优惠金额
     * @return 活动是否禁止参与，及当前允许额度
     */
    private CommonBoolDto<BigDecimal> checkPerFinalEnableQuota(ActivityQuotaRuleEntity quota,
                                                               BigDecimal vauleCondition,
                                                               String conditionFlag,
                                                               BigDecimal applyNum,
                                                               BigDecimal profitPerAmount) {
        CommonBoolDto<BigDecimal> result = new CommonBoolDto<BigDecimal>(true);
        result.setData(applyNum);
        if (CommonUtils.isGtZeroDecimal(profitPerAmount)) {
            result.setSuccess(false);
            result.setDesc("每个单位优惠金额必须大于0");
            result.setData(BigDecimal.ZERO);
            return result;
        }
        //校验限额
        if (quota != null) {
            //满足优惠活动条件的时最低优惠数量
            BigDecimal countCondition = BigDecimal.ZERO;
            //满足优惠活动条件的时最低优惠金额
            BigDecimal fundCondition = BigDecimal.ZERO;
            if (CommonUtils.isGtZeroDecimal(vauleCondition)) {
                if (CommonDict.IF_YES.getCode().equals(conditionFlag)) {
                    fundCondition = vauleCondition;
                    countCondition = vauleCondition.divide(profitPerAmount);
                } else {
                    fundCondition = vauleCondition.multiply(profitPerAmount);
                    countCondition = vauleCondition;
                }
            }

            //1.校验【每笔最大优惠“数量”】
            applyNum = CommonUtils.GetEnableUseQuota(countCondition, applyNum, quota.getPerMaxCount());
            if (applyNum.compareTo(BigDecimal.ZERO) <= 0) {
                result.setSuccess(false);
                result.setDesc(String.format("每笔最大优惠“数量”校验不通过最少需要满足数量{0}，申请优惠数量{1}，实际剩余数量{2}", countCondition, applyNum, quota.getPerMaxCount()));
                result.setData(BigDecimal.ZERO);
                return result;
            }
            //申请优惠总额
            BigDecimal profitApplyAmount = profitPerAmount.multiply(applyNum);

            //2.校验【每笔最大优惠“金额”】
            BigDecimal enableProfitAmount = CommonUtils.GetEnableUseQuota(fundCondition, profitApplyAmount, quota.getPerMaxAmount());
            if (applyNum.compareTo(BigDecimal.ZERO) <= 0) {
                result.setSuccess(false);
                result.setDesc(String.format("每笔最大优惠“金额”校验不通过最少需要满足优惠金额{0}，申请优惠金额{1}，实际剩余优惠金额额度{2}", fundCondition, applyNum, quota.getPerMaxCount()));
                result.setData(BigDecimal.ZERO);
                return result;
            }
            //最终能优惠的金额转换为可优惠的数量
            applyNum = enableProfitAmount.divide(profitPerAmount).setScale(0, BigDecimal.ROUND_DOWN);//重量类商品也是按1单位数量参与活动

        }
        result.setData(applyNum);
        return result;
    }


    /**
     * 获取“按用户”或按“所有用户”限额最终允许参与活动数量
     *
     * @param diffInfo        实际额度与额度限制配置差值情况
     * @param conditionFlag   0-vauleCondition标识数量 1-标识金额
     * @param vauleCondition  满足优惠活动条件的时最低优惠数量
     * @param applyNum        适用数量初始值：需校验的值
     * @param profitPerAmount 单个商品优惠金额
     * @return 活动是否禁止参与，及当前允许额度
     */
    private CommonBoolDto<BigDecimal> checkTotalFinalEnableQuota(QuotaIndexDiffInfo diffInfo,
                                                                 BigDecimal vauleCondition,
                                                                 String conditionFlag,
                                                                 BigDecimal applyNum,
                                                                 BigDecimal profitPerAmount) {
        CommonBoolDto<BigDecimal> result = new CommonBoolDto<>(true);
        result.setData(applyNum);
        if (CommonUtils.isGtZeroDecimal(profitPerAmount)) {
            result.setSuccess(false);
            result.setDesc("每个单位数量优惠金额必须大于0");
            result.setData(BigDecimal.ZERO);
            return result;
        }

        if (CommonUtils.isGtZeroDecimal(applyNum)) {
            return result;
        }
        //校验限额
        if (diffInfo != null) {
            //满足优惠活动条件的时最低优惠数量
            BigDecimal countCondition = BigDecimal.ZERO;
            //满足优惠活动条件的时最低优惠金额
            BigDecimal fundCondition = BigDecimal.ZERO;
            if (CommonUtils.isGtZeroDecimal(vauleCondition)) {
                if (CommonDict.IF_YES.getCode().equals(conditionFlag)) {
                    fundCondition = vauleCondition;
                    countCondition = vauleCondition.divide(profitPerAmount);
                } else {
                    fundCondition = vauleCondition.multiply(profitPerAmount);
                    countCondition = vauleCondition;
                }
            }

            //1.校验【最大优惠“数量”】
            applyNum = CommonUtils.GetEnableUseQuota(countCondition, applyNum, diffInfo.getClientMinDiffCount());
            if (applyNum.compareTo(BigDecimal.ZERO) <= 0) {
                result.setSuccess(false);
                result.setDesc(String.format("最大优惠“数量”校验不通过最少需要满足数量{0}，申请优惠数量{1}，实际剩余数量{2}", countCondition, applyNum, diffInfo.getClientMinDiffCount()));
                result.setData(BigDecimal.ZERO);
                return result;
            }
            //申请优惠总额
            BigDecimal profitApplyAmount = profitPerAmount.multiply(applyNum);

            //2.校验【每笔最大优惠“金额”】
            BigDecimal enableProfitAmount = CommonUtils.GetEnableUseQuota(fundCondition, profitApplyAmount, diffInfo.getClientMinDiffAmount());
            if (applyNum.compareTo(BigDecimal.ZERO) <= 0) {
                result.setSuccess(false);
                result.setDesc(String.format("最大优惠“金额”校验不通过最少需要满足优惠金额{0}，申请优惠金额{1}，实际剩余优惠金额额度{2}", fundCondition, applyNum, diffInfo.getClientMinDiffAmount()));
                result.setData(BigDecimal.ZERO);
                return result;
            }
            //最终能优惠的金额转换为可优惠的数量
            applyNum = enableProfitAmount.divide(profitPerAmount).setScale(0, BigDecimal.ROUND_DOWN);//重量类商品也是按1单位数量参与活动

        }
        result.setData(applyNum);
        return result;
    }
}
