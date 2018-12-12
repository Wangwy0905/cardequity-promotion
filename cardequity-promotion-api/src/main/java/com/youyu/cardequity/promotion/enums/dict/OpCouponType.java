package com.youyu.cardequity.promotion.enums.dict;

/**
 * Created by caiyi on 2018/12/12.
 */
public enum OpCouponType {
    GETRULE("0", "获取"),
    USERULE("1", "使用"),
;

    public static final String DICTID = "100179";
    public static final String DICTNAME = "OpCouponType";
    public static final String DICTKIND = "2";
    public static final String DICTCOMMENT = "优惠券规则操作类型";

    private final String dictValue;
    private final String dictComment;

    OpCouponType(String dictValue, String dictComment) {
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
