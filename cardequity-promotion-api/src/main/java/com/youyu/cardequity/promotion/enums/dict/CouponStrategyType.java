package com.youyu.cardequity.promotion.enums.dict;

/**
 * Created by caiyi on 2018/12/21.
 */
public enum CouponStrategyType {
    discount("0", "折扣券"),
    stage("1", "门槛优惠券"),
    fix("2", "无门槛优惠券"),
    random("3", "随机优惠券"),
    equalstage("4", "等阶门槛优惠券"),
    ;

    public static final String DICTID = "100179";
    public static final String DICTNAME = "CouponStrategyType";
    public static final String DICTKIND = "2";
    public static final String DICTCOMMENT = "优惠策略类型";

    private final String dictValue;
    private final String dictComment;

    CouponStrategyType(String dictValue, String dictComment) {
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
