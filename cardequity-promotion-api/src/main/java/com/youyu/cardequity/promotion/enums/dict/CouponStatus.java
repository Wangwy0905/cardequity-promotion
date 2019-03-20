package com.youyu.cardequity.promotion.enums.dict;

/**
 * Created by caiyi on 2019/1/16.
 */
public enum CouponStatus {

    NO("0", "下架"),
    YES("1", "上架"),

            ;

    public static final String DICTID = "100179";
    public static final String DICTNAME = "CouponStatus";
    public static final String DICTKIND = "2";
    public static final String DICTCOMMENT = "优惠券状态";

    private final String dictValue;
    private final String dictComment;

    CouponStatus(String dictValue, String dictComment) {
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
