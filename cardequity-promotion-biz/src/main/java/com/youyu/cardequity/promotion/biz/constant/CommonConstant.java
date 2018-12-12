package com.youyu.cardequity.promotion.biz.constant;

import java.math.BigDecimal;

/**
 * Created by caiyi on 2018/12/12.
 */
public interface CommonConstant {
    /**
     * 数值参数的边界有效上限
     */
    BigDecimal IGNOREVALUE=new BigDecimal("999999999");


    /**
     * 业务中指定的通配符
     */
    String WILDCARD="*";
}
