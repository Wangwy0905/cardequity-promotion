package com.youyu.cardequity.promotion.biz.strategy.activity;

import com.youyu.cardequity.promotion.biz.dal.dao.ClientTakeInActivityMapper;
import com.youyu.cardequity.promotion.biz.dal.entity.ActivityProfitEntity;
import com.youyu.cardequity.promotion.biz.dal.entity.ActivityQuotaRuleEntity;
import com.youyu.cardequity.promotion.biz.utils.CommonUtils;
import com.youyu.cardequity.promotion.dto.other.ClientCoupStatisticsQuotaDto;
import com.youyu.cardequity.promotion.dto.other.CommonBoolDto;
import com.youyu.cardequity.promotion.dto.other.OrderProductDetailDto;
import com.youyu.cardequity.promotion.enums.CommonDict;
import com.youyu.cardequity.promotion.vo.domain.QuotaIndexDiffInfo;
import com.youyu.cardequity.promotion.vo.rsp.UseActivityRsp;
import org.springframework.beans.factory.annotation.Autowired;

import java.math.BigDecimal;
import java.util.List;

import static com.youyu.cardequity.promotion.enums.ResultCode.*;
import static com.youyu.cardequity.promotion.enums.ResultCode.COUPON_FAIL_COUNT_PERDATEQUOTA;

/**
 * 活动策略
 * @author 徐长焕
 * @date 2018-12-20
 */
public abstract class ActivityStrategy {

    @Autowired
    private ClientTakeInActivityMapper clientTakeInActivityMapper;

    public abstract UseActivityRsp applyActivity(ActivityProfitEntity item, List<OrderProductDetailDto> productList);


    /**
     * 统计允许差值
     * @param quota  定义的额度
     * @param statisticsQuotaDto 统计的数据
     * @return 定义额度与统计数据之间差额
     */
    QuotaIndexDiffInfo statisticsQuotaIndexMinDiff(ActivityQuotaRuleEntity quota,
                                                   ClientCoupStatisticsQuotaDto statisticsQuotaDto){
        QuotaIndexDiffInfo diffInfo=new QuotaIndexDiffInfo();
         if (quota==null || statisticsQuotaDto==null){
             return diffInfo;
         }

         //指定客户的统计
         if (!CommonUtils.isEmptyorNull(statisticsQuotaDto.getClientId())) {
             //1.个人活动金额最大限额差值
             String flag = CommonUtils.isQuotaValueNeedValidFlag(quota.getPersonMaxAmount());
             if (CommonDict.CONTINUEVALID.getCode().equals(flag)) {
                 diffInfo.setClientDiffAmount(quota.getPersonMaxAmount().subtract(statisticsQuotaDto.getClientAmount()));
             } else if (CommonDict.FAILVALID.getCode().equals(flag)) {
                 diffInfo.setClientDiffAmount(BigDecimal.ZERO);
             }
             //保存最小的值
             if (diffInfo.getClientDiffAmount().compareTo(diffInfo.getClientMinDiffAmount()) < 0)
                 diffInfo.setClientMinDiffAmount(diffInfo.getClientDiffAmount());


             flag = CommonUtils.isQuotaValueNeedValidFlag(quota.getPerDateAndAccMaxAmount());
             if (CommonDict.CONTINUEVALID.getCode().equals(flag)) {
                 diffInfo.setClientDiffPerDateAmount(quota.getPerDateAndAccMaxAmount().subtract(statisticsQuotaDto.getClientPerDateAmount()));
             } else if (CommonDict.FAILVALID.getCode().equals(flag)) {
                 diffInfo.setClientDiffPerDateAmount(BigDecimal.ZERO);
             }
             //保存最小的值
             if (diffInfo.getClientDiffPerDateAmount().compareTo(diffInfo.getClientMinDiffAmount()) < 0)
                 diffInfo.setClientMinDiffAmount(diffInfo.getClientMinDiffAmount());

             //3.个人活动参与商品数量最大限额差值
              flag = CommonUtils.isQuotaValueNeedValidFlag(quota.getPersonMaxCount());
             if (CommonDict.CONTINUEVALID.getCode().equals(flag)) {
                 diffInfo.setClientMinDiffCount(quota.getPersonMaxCount().subtract(statisticsQuotaDto.getClientCount()));
             } else if (CommonDict.FAILVALID.getCode().equals(flag)) {
                 diffInfo.setClientMinDiffCount(BigDecimal.ZERO);
             }
             //保存最小的值
             if (diffInfo.getClientDiffCount().compareTo(diffInfo.getClientMinDiffCount()) < 0)
                 diffInfo.setClientMinDiffCount(diffInfo.getClientDiffCount());


             flag = CommonUtils.isQuotaValueNeedValidFlag(quota.getPerDateAndAccMaxCount());
             if (CommonDict.CONTINUEVALID.getCode().equals(flag)) {
                 diffInfo.setClientDiffPerDateCount(quota.getPerDateAndAccMaxCount().subtract(statisticsQuotaDto.getClientPerDateCount()));
             } else if (CommonDict.FAILVALID.getCode().equals(flag)) {
                 diffInfo.setClientDiffPerDateCount(BigDecimal.ZERO);
             }
             //保存最小的值
             if (diffInfo.getClientDiffPerDateCount().compareTo(diffInfo.getClientMinDiffCount()) < 0)
                 diffInfo.setClientMinDiffCount(diffInfo.getClientMinDiffCount());
         }else{
             //1.活动金额最大限额差值
             String flag = CommonUtils.isQuotaValueNeedValidFlag(quota.getMaxAmount());
             if (CommonDict.CONTINUEVALID.getCode().equals(flag)) {
                 diffInfo.setClientDiffAmount(quota.getMaxAmount().subtract(statisticsQuotaDto.getClientAmount()));
             } else if (CommonDict.FAILVALID.getCode().equals(flag)) {
                 diffInfo.setClientDiffAmount(BigDecimal.ZERO);
             }
             //保存最小的值
             if (diffInfo.getClientDiffAmount().compareTo(diffInfo.getClientMinDiffAmount()) < 0)
                 diffInfo.setClientMinDiffAmount(diffInfo.getClientDiffAmount());


             flag = CommonUtils.isQuotaValueNeedValidFlag(quota.getPerDateMaxAmount());
             if (CommonDict.CONTINUEVALID.getCode().equals(flag)) {
                 diffInfo.setClientDiffPerDateAmount(quota.getPerDateMaxAmount().subtract(statisticsQuotaDto.getClientPerDateAmount()));
             } else if (CommonDict.FAILVALID.getCode().equals(flag)) {
                 diffInfo.setClientDiffPerDateAmount(BigDecimal.ZERO);
             }
             //保存最小的值
             if (diffInfo.getClientDiffPerDateAmount().compareTo(diffInfo.getClientMinDiffAmount()) < 0)
                 diffInfo.setClientMinDiffAmount(diffInfo.getClientMinDiffAmount());

             //3.个人活动参与商品数量最大限额差值
             flag = CommonUtils.isQuotaValueNeedValidFlag(quota.getMaxCount());
             if (CommonDict.CONTINUEVALID.getCode().equals(flag)) {
                 diffInfo.setClientMinDiffCount(quota.getMaxCount().subtract(statisticsQuotaDto.getClientCount()));
             } else if (CommonDict.FAILVALID.getCode().equals(flag)) {
                 diffInfo.setClientMinDiffCount(BigDecimal.ZERO);
             }
             //保存最小的值
             if (diffInfo.getClientDiffCount().compareTo(diffInfo.getClientMinDiffCount()) < 0)
                 diffInfo.setClientMinDiffCount(diffInfo.getClientDiffCount());


             flag = CommonUtils.isQuotaValueNeedValidFlag(quota.getPerDateMaxCount());
             if (CommonDict.CONTINUEVALID.getCode().equals(flag)) {
                 diffInfo.setClientDiffPerDateCount(quota.getPerDateMaxCount().subtract(statisticsQuotaDto.getClientPerDateCount()));
             } else if (CommonDict.FAILVALID.getCode().equals(flag)) {
                 diffInfo.setClientDiffPerDateCount(BigDecimal.ZERO);
             }
             //保存最小的值
             if (diffInfo.getClientDiffPerDateCount().compareTo(diffInfo.getClientMinDiffCount()) < 0)
                 diffInfo.setClientMinDiffCount(diffInfo.getClientMinDiffCount());

         }

        return null;

    }





    /**
     * 校验个人的优惠限额
     *
     * @param quota    活动额度信息
     * @param clientId 指定校验的客户
     * @return 开发日志
     * 1004258-徐长焕-20181213 新增
     */
    CommonBoolDto<ClientCoupStatisticsQuotaDto> checkActivityPersonQuota(ActivityQuotaRuleEntity quota,
                                                                         String clientId) {
        CommonBoolDto<ClientCoupStatisticsQuotaDto> dto = new CommonBoolDto<>(true);
        //存在规则才进行校验
        /**
         * 每客每天最大优惠额
         * 每客最大优惠额
         * 每天最大优惠额
         * 最大优惠金额(资金池数量)
         * 最大发放数量(券池数量)
         */

        //大于等于该999999999值都标识不控制
        if (quota != null) {
            ClientCoupStatisticsQuotaDto statisticsQuotaDto = statisticsCouponQuota(clientId, quota.getActivityId(), "");
            dto.setData(statisticsQuotaDto);

            //校验每客每天最大优惠额
            String validflag = CommonUtils.isQuotaValueNeedValidFlag(quota.getPerDateAndAccMaxAmount());
            if (CommonDict.CONTINUEVALID.getCode().equals(validflag)) {
                //判断是否客户当日已领取的优惠金额是否超限
                if (quota.getPerDateAndAccMaxAmount().compareTo(statisticsQuotaDto.getClientPerDateAmount()) <= 0) {
                    dto.setSuccess(false);
                    dto.setDesc(COUPON_FAIL_PERACCANDDATEQUOTA.getFormatDesc(quota.getPerDateAndAccMaxAmount(), statisticsQuotaDto.getClientPerDateAmount(), clientId));
                    return dto;
                }

            } else if (CommonDict.FAILVALID.getCode().equals(validflag)) {
                dto.setSuccess(false);
                dto.setDesc(COUPON_FAIL_PERACCANDDATEQUOTA.getFormatDesc(BigDecimal.ZERO, "忽略", clientId));
                return dto;
            }

            //校验每客最大优惠额
            validflag = CommonUtils.isQuotaValueNeedValidFlag(quota.getPersonMaxAmount());
            if (CommonDict.CONTINUEVALID.getCode().equals(validflag)) {
                //判断是否客户已领取的优惠金额是否超限
                if (quota.getPerMaxAmount().compareTo(statisticsQuotaDto.getClientAmount()) <= 0) {
                    dto.setSuccess(false);
                    dto.setDesc(COUPON_FAIL_PERACCQUOTA.getFormatDesc(quota.getPerMaxAmount(), statisticsQuotaDto.getClientAmount(), clientId));
                    return dto;
                }
            } else if (CommonDict.FAILVALID.getCode().equals(validflag)) {
                dto.setSuccess(false);
                dto.setDesc(COUPON_FAIL_PERACCQUOTA.getFormatDesc(BigDecimal.ZERO, "忽略", clientId));
                return dto;
            }

            BigDecimal dateMaxAmount = BigDecimal.ZERO;
            if (quota.getPerDateAndAccMaxCount() != null)
                dateMaxAmount = new BigDecimal(quota.getPerDateAndAccMaxCount().toString());
            //3.校验每天最大数量PerDateMaxCount
            validflag = CommonUtils.isQuotaValueNeedValidFlag(dateMaxAmount);
            if (CommonDict.CONTINUEVALID.getCode().equals(validflag)) {//0-验证不通过，1-验证通过，2-需要继续验证

                //判断是否所有客户已领取的优惠金额是否超限
                if (dateMaxAmount.compareTo(statisticsQuotaDto.getClientPerDateCount()) <= 0) {
                    dto.setSuccess(false);
                    dto.setDesc(COUPON_FAIL_COUNT_PERACCANDDATEQUOTA.getFormatDesc(dateMaxAmount, statisticsQuotaDto.getClientPerDateCount(), clientId));
                    return dto;
                }
            } else if (CommonDict.FAILVALID.getCode().equals(validflag)) {
                dto.setSuccess(false);
                dto.setDesc(COUPON_FAIL_COUNT_PERACCANDDATEQUOTA.getFormatDesc(BigDecimal.ZERO, "忽略", clientId));
                return dto;
            }

            BigDecimal maxCount = BigDecimal.ZERO;
            if (quota.getMaxCount() != null)
                maxCount = new BigDecimal(quota.getMaxCount().toString());
            //4.校验每客每天最大优惠额
            validflag = CommonUtils.isQuotaValueNeedValidFlag(maxCount);
            if (CommonDict.CONTINUEVALID.getCode().equals(validflag)) {//0-验证不通过，1-验证通过，2-需要继续验证

                //判断是否所有客户已领取的优惠金额是否超限
                if (maxCount.compareTo(statisticsQuotaDto.getClientCount()) <= 0) {
                    dto.setSuccess(false);
                    dto.setDesc(COUPON_FAIL_COUNT_PERACCQUOTA.getFormatDesc(maxCount, statisticsQuotaDto.getClientCount(), clientId));
                    return dto;
                }
            } else if (CommonDict.FAILVALID.getCode().equals(validflag)) {
                dto.setSuccess(false);
                dto.setDesc(COUPON_FAIL_COUNT_PERACCQUOTA.getFormatDesc(BigDecimal.ZERO, "忽略", clientId));
                return dto;
            }
        }


        return dto;
    }

    /**
     * 校验所有的优惠限额
     *
     * @param quota 活动额度信息
     * @return 开发日志
     * 1004258-徐长焕-20181213 新增
     */
    CommonBoolDto<ClientCoupStatisticsQuotaDto> checkActivityAllQuota(ActivityQuotaRuleEntity quota) {
        CommonBoolDto<ClientCoupStatisticsQuotaDto> dto = new CommonBoolDto<>(true);

        if (quota != null) {
                ClientCoupStatisticsQuotaDto  statisticsQuotaDto = statisticsCouponQuota("", quota.getActivityId(), "");

            dto.setData(statisticsQuotaDto);
            //1.校验所有客户每天最大优惠额getPerDateMaxAmount
            String validflag = CommonUtils.isQuotaValueNeedValidFlag(quota.getPerDateMaxAmount());
            if (CommonDict.CONTINUEVALID.getCode().equals(validflag)) {

                //判断是否所有客户当日已领取的优惠金额是否超限
                if (quota.getPerDateMaxAmount().compareTo(statisticsQuotaDto.getClientPerDateAmount()) <= 0) {
                    dto.setSuccess(false);
                    dto.setDesc(COUPON_FAIL_PERDATEQUOTA.getFormatDesc(quota.getPerDateMaxAmount(), statisticsQuotaDto.getClientPerDateAmount(), quota.getActivityId()));
                    return dto;
                }
            } else if (CommonDict.FAILVALID.getCode().equals(validflag)) {
                dto.setSuccess(false);
                dto.setDesc(COUPON_FAIL_PERDATEQUOTA.getFormatDesc(BigDecimal.ZERO, "忽略", quota.getActivityId()));
                return dto;
            }

            //2.校验所有客户最大优惠额getMaxAmount
            validflag = CommonUtils.isQuotaValueNeedValidFlag(quota.getMaxAmount());
            if (CommonDict.CONTINUEVALID.getCode().equals(validflag)) {

                //判断是否所有客户已领取的优惠金额是否超限
                if (quota.getMaxAmount().compareTo(statisticsQuotaDto.getClientAmount()) <= 0) {
                    dto.setSuccess(false);
                    dto.setDesc(COUPON_FAIL_QUOTA.getFormatDesc(quota.getMaxAmount(), statisticsQuotaDto.getClientAmount(), quota.getActivityId()));
                    return dto;
                }

            } else if (CommonDict.FAILVALID.getCode().equals(validflag)) {
                dto.setSuccess(false);
                dto.setDesc(COUPON_FAIL_QUOTA.getFormatDesc(BigDecimal.ZERO, "忽略", quota.getActivityId()));
                return dto;
            }

            BigDecimal maxCount = BigDecimal.ZERO;
            if (quota.getMaxCount() != null)
                maxCount = new BigDecimal(quota.getMaxCount().toString());
            //3.校验所有客户最大领取数量maxCount:quota.getMaxCount()
            validflag = CommonUtils.isQuotaValueNeedValidFlag(maxCount);
            if (CommonDict.CONTINUEVALID.getCode().equals(validflag)) {//0-验证不通过，1-验证通过，2-需要继续验证

                //判断是否所有客户已领取的优惠金额是否超限
                if (maxCount.compareTo(statisticsQuotaDto.getClientCount()) <= 0) {
                    dto.setSuccess(false);
                    dto.setDesc(COUPON_FAIL_COUNT_QUOTA.getFormatDesc(maxCount, statisticsQuotaDto.getClientCount(), quota.getActivityId()));
                    return dto;
                }
            } else if (CommonDict.FAILVALID.getCode().equals(validflag)) {
                dto.setSuccess(false);
                dto.setDesc(COUPON_FAIL_COUNT_QUOTA.getFormatDesc(BigDecimal.ZERO, "忽略", quota.getActivityId()));
                return dto;
            }

            BigDecimal dateMaxAmount = BigDecimal.ZERO;
            if (quota.getPerDateMaxAmount() != null)
                dateMaxAmount = new BigDecimal(quota.getPerDateMaxAmount().toString());
            //4.校验每天最大数量PerDateMaxCount
            validflag = CommonUtils.isQuotaValueNeedValidFlag(dateMaxAmount);
            if (CommonDict.CONTINUEVALID.getCode().equals(validflag)) {//0-验证不通过，1-验证通过，2-需要继续验证

                //判断是否所有客户已领取的优惠金额是否超限
                if (dateMaxAmount.compareTo(statisticsQuotaDto.getClientPerDateCount()) <= 0) {
                    dto.setSuccess(false);
                    dto.setDesc(COUPON_FAIL_COUNT_PERDATEQUOTA.getFormatDesc(dateMaxAmount, statisticsQuotaDto.getClientPerDateCount(), quota.getActivityId()));
                    return dto;
                }
            } else if (CommonDict.FAILVALID.getCode().equals(validflag)) {
                dto.setSuccess(false);
                dto.setDesc(COUPON_FAIL_COUNT_PERDATEQUOTA.getFormatDesc(BigDecimal.ZERO, "忽略", quota.getActivityId()));
                return dto;
            }

        }

        return dto;
    }

    /**
     * 统计指定活动的参与情况信息
     *
     * @param activityId 指定统计的活动
     * @return 开发日志
     * 1004258-徐长焕-20181213 新增
     */
    private ClientCoupStatisticsQuotaDto statisticsCouponQuota(String clientId,
                                                               String activityId,
                                                               String stageId) {
        //统计所有客户领取的优惠券金额总额，直接通过sql统计增加效率
        ClientCoupStatisticsQuotaDto dto = clientTakeInActivityMapper.statisticsCouponByCommon(clientId, activityId, stageId);
        if (dto == null)
            dto = new ClientCoupStatisticsQuotaDto();
        dto.setCouponId(activityId);
        //已经获取自数据库
        dto.setStatisticsFlag(CommonDict.IF_YES.getCode());

        return dto;
    }

}
