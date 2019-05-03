package com.youyu.cardequity.promotion.biz.constant;

/**
 * @Auther: zjm
 * @Date: 2019-04-30
 * @Description:
 */
public class ClientCouponStatusConstant {
    /**
     * 券状态：0-未使用；2-已使用；3-已过期;4-发放失败或未发放
     */
    public static final String COUPON_NOT_USED = "0";
    public static final String COUPON_IS_USED = "2";
    public static final String COUPON_IS_INVALID = "3";
    public static final String ISSUED_FAILED_OR_NOTISSUED = "4";
}
