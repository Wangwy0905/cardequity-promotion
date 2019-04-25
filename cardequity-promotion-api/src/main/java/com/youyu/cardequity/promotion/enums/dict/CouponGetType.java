package com.youyu.cardequity.promotion.enums.dict;

/**
 * Created by caiyi on 2019/3/4.
 */
public enum CouponGetType {
    AUTO("0", "后台发放（自动发送）"),
    HANLD("1", "手动"),
    GRANT("2", "指定平台发放"),
            ;

    public static final String DICTID = "100179";
    public static final String DICTNAME = "CouponGetType";
    public static final String DICTKIND = "2";
    public static final String DICTCOMMENT = "优惠券获取方式";

    private final String dictValue;
    private final String dictComment;

    CouponGetType(String dictValue, String dictComment) {
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
