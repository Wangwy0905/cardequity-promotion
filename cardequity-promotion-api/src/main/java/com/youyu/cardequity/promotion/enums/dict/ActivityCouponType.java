package com.youyu.cardequity.promotion.enums.dict;

/**
 * Created by caiyi on 2018/12/25.
 */
public enum ActivityCouponType {
    MAXQUOTA("0", "任选限额"),
    DISCOUNT("1", "折扣"),
    PRICE("2", "优惠价"),
    CASH("3", "现金立减"),
    BACKCOUPON("4", "返券"),
    EQUALSTAGECASH("5", "等阶门槛立减"),
    QUOTASEND("6", "满送活动"),
    ;

    public static final String DICTID = "100179";
    public static final String DICTNAME = "ActivityCouponType";
    public static final String DICTKIND = "2";
    public static final String DICTCOMMENT = "活动类型";

    private final String dictValue;
    private final String dictComment;

    ActivityCouponType(String dictValue, String dictComment) {
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
