package com.youyu.cardequity.promotion.constant;

import java.math.BigDecimal;

/**
 * Created by caiyi on 2018/12/29.
 */
public interface CommonConstant {
    /**
     * 数值参数的边界有效上限
     */
    BigDecimal IGNOREVALUE = new BigDecimal("999999999");

    /**
     * 数值参数的边界有效上限
     */
    Integer IGNOREINTVALUE = new Integer("999999999");


    /**
     * 业务中指定的通配符
     */
    String WILDCARD = "*";

    /**
     * 权益专属定义0-普通 1-会员专属 2-银行卡专属
     */
    String PROMOTION_APPLYTYPE_COMMON = "0";
    String PROMOTION_APPLYTYPE_MEMBER = "1";
    String PROMOTION_APPLYTYPE_BANKCODE = "2";

    /**
     * 查询优惠信息时是否排除掉因额度和领取频率限制的
     */
    String EXCLUSIONFLAG_ALL = "0";
    String EXCLUSIONFLAG_ACCURATE = "1";


    /**
     * 是否新用户
     */
    String USENEWREGISTER_NO = "0";
    String USENEWREGISTER_YES = "1";

    /**
     * 领取状态:0-可领取 1-已领取 2-已使用 3-过期未使用 4-未开始 5-可使用 6-该用户已领完 7-可继续领取
     */
    String OBTAIN_STATE_NO = "0";
    String OBTAIN_STATE_YES = "1";
    String OBTAIN_STATE_USE = "2";
    String OBTAIN_STATE_OVERDUE = "3";
    String OBTAIN_STATE_UNSTART = "4";
    String OBTAIN_STATE_14 = "5";
    String OBTAIN_STATE_PERSON_OVER = "6";
    String OBTAIN_STATE_CONTINUE = "7";

    /**
     * 领取状态:0-消费券 1-运费券
     */
    String VIEW_COUPONTYPE_COMMON = "0";
    String VIEW_COUPONTYPE_TRANSFER = "1";

    /**
     * 活动状态 0-正常 1-额度已抢完 2-已过期 3-未开始
     */
    String VIEW_ACTIVITYSTATUS_COMMON = "0";
    String VIEW_ACTIVITYSTATUS_NOT_QUOTA = "1";
    String VIEW_ACTIVITYSTATUS_OVERDUE = "2";
    String VIEW_ACTIVITYSTATUSE_UNSTART = "3";



}
