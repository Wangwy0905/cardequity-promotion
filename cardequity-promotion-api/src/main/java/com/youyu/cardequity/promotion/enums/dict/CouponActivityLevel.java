package com.youyu.cardequity.promotion.enums.dict;

/**
 * Created by caiyi on 2018/12/20.
 */
public enum CouponActivityLevel {
    PART("0", "自定义"),
    GLOBAL("1", "全局")

            ;

    public static final String DICTID = "100179";
    public static final String DICTNAME = "CouponActivityLevel";
    public static final String DICTKIND = "2";
    public static final String DICTCOMMENT = "券或活动等级";

    private final String dictValue;
    private final String dictComment;

    CouponActivityLevel(String dictValue, String dictComment) {
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
