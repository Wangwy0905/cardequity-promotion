package com.youyu.cardequity.promotion.enums.dict;

/**
 * Created by caiyi on 2018/12/20.
 */
public enum CouponApplyProductStage {
    CONDITION("0", "适用算入门槛内商品"),
    ALL("1", "订单中涉及活动或券定义适用所有商品都打折"),
    ;

    public static final String DICTID = "100179";
    public static final String DICTNAME = "CouponApplyProductStage";
    public static final String DICTKIND = "2";
    public static final String DICTCOMMENT = "折扣使用策略";

    private final String dictValue;
    private final String dictComment;

    CouponApplyProductStage(String dictValue, String dictComment) {
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
