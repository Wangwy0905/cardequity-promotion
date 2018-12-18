package com.youyu.cardequity.promotion.enums.dict;

/**
 * Created by caiyi on 2018/12/14.
 */
public enum ActiveOrCouponType {
    COUPON("0", "优惠券"),
    ACTIVITY("1", "活动"),

            ;

    public static final String DICTID = "100179";
    public static final String DICTNAME = "ActiveOrCouponType";
    public static final String DICTKIND = "2";
    public static final String DICTCOMMENT = "优惠券或活动标识";

    private final String dictValue;
    private final String dictComment;

    ActiveOrCouponType(String dictValue, String dictComment) {
        this.dictValue = dictValue;
        this.dictComment = dictComment;
    }

    public String getDictValue() {
        return dictValue;
    }

    public String getDictComment() {
        return dictComment;
    }
}
