package com.youyu.cardequity.promotion.constant;

import java.math.BigDecimal;

/**
 * Created by caiyi on 2018/12/29.
 */
public interface CommonConstant {
    /**
     * 数值参数的边界有效上限
     */
    BigDecimal IGNOREVALUE=new BigDecimal("999999999");

    /**
     * 数值参数的边界有效上限
     */
    Integer IGNOREINTVALUE=new Integer("999999999");


    /**
     * 业务中指定的通配符
     */
    String WILDCARD="*";

    /**
     * 权益专属定义0-普通 1-会员专属 2-银行卡专属
     */
    String PROMOTION_APPLYTYPE_COMMON="0";
    String PROMOTION_APPLYTYPE_MEMBER="1";
    String PROMOTION_APPLYTYPE_BANKCODE="2";
}
