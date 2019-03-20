package com.youyu.cardequity.promotion.enums.dict;

/**
 * Created by caiyi on 2018/12/25.
 */
public enum CouponUseType {
    ORDER("0", "下单时注销"),
    CONFIRM("1", "确认订单时注销"),

    ;

    public static final String DICTID = "100179";
    public static final String DICTNAME = "CouponUseType";
    public static final String DICTKIND = "2";
    public static final String DICTCOMMENT = "优惠券使用类型";

    private final String dictValue;
    private final String dictComment;

    CouponUseType(String dictValue, String dictComment) {
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
