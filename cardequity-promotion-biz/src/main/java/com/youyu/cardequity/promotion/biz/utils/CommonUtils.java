package com.youyu.cardequity.promotion.biz.utils;

import com.youyu.cardequity.promotion.biz.constant.CommonConstant;
import com.youyu.cardequity.promotion.enums.CommonDict;

import java.math.BigDecimal;
import java.util.UUID;

/**
 * Created by caiyi on 2018/12/12.
 */
public class CommonUtils {

    /**
     * 获取uuid
     * @return
     */
    public static String getUUID() {
        String uuid = UUID.randomUUID().toString().replace("-", "").toLowerCase();
        return uuid;
    }


    /**
     * 判断字符串是否为空或null
     *
     * @param target
     * @return
     */
    public static boolean isEmptyorNull(String target) {
        if (target == null)
            return true;
        if (target.isEmpty() || "".equals(target))
            return true;
        return false;
    }

    /**
     * 判断字符串是否为空或null
     *
     * @param target
     * @return
     */
    public static boolean isGtZeroDecimal(BigDecimal target) {
        if (target == null)
            return false;
        if (target.compareTo(BigDecimal.ZERO)<=0)
            return false;
        return true;
    }

    /**
     * 为空忽略或匹配成功返回true
     *
     * @param source
     * @param target
     * @return
     */
    public static boolean isEmptyIgnoreOrWildcardOrContains(String source, String target) {
        if (target == null ||
                source == null ||
                source.equals(CommonConstant.WILDCARD) ||
                source.contains(target)) {
            return true;
        }
        return false;
    }

    /**
     * 额度类参数需要验证的情况：0-验证不通过，1-验证通过，2-需要继续验证
     *
     * @param value 额度类参数值
     * @return
     */
    public static String isQuotaValueNeedValidFlag(BigDecimal value) {
        String validflag = CommonDict.CONTINUEVALID.getCode();//0-验证不通过，1-验证通过，2-需要继续验证

        //额度有效数值为[0,999999999),1.达到或超过上界标识不控制；2.没设置值保护为不控制
        if (value == null ||
                value.compareTo(CommonConstant.IGNOREVALUE) >= 0) {

            validflag = CommonDict.PASSVALID.getCode();
        } else if (value.compareTo(BigDecimal.ZERO) <= 0) {
            validflag = CommonDict.FAILVALID.getCode();
        }
        return validflag;
    }


    /**
     * 计算适用的数量
     * @param maxValue 实际允许最大额度
     * @param applyvalue 条件值
     * @param valueCondition  指定校验适用数量
     * @return
     */
    public static BigDecimal  GetEnableUseQuota(BigDecimal valueCondition,
                                  BigDecimal applyvalue,
                                  BigDecimal maxValue){
        BigDecimal realUsevalue=applyvalue;
        //对参数保护
        if (CommonDict.CONTINUEVALID.getCode().equals(CommonUtils.isQuotaValueNeedValidFlag(maxValue))) {
            //限额<门槛数量
            if (maxValue.compareTo(valueCondition) < 0) {
                //log.info("该优惠子编号{}中该客户选购中可参加活动商品数量限制最多为{}", stage.getId(), personDiffInfo.getClientDiffCount());
                return BigDecimal.ZERO;
            }
            //指定校验适用数量>限额>门槛数量：min()
            if (maxValue.compareTo(applyvalue) < 0)
                realUsevalue = maxValue;
        }
        return realUsevalue;
    }

}
