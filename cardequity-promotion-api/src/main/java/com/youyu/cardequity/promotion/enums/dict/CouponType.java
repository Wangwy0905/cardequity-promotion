package com.youyu.cardequity.promotion.enums.dict;

/**
 * Created by caiyi on 2018/12/18.
 */
public enum CouponType {
    MONEYBAG("0", "红包"),
    COUPON("1", "优惠券"),
    TRANSFERFARE("2", "运费券"),
    ;

    public static final String DICTID = "100179";
    public static final String DICTNAME = "CouponType";
    public static final String DICTKIND = "2";
    public static final String DICTCOMMENT = "优惠券类型";

    private final String dictValue;
    private final String dictComment;

    CouponType(String dictValue, String dictComment) {
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
