package com.youyu.cardequity.promotion.enums.dict;

/**
 * Created by caiyi on 2018/12/14.
 */
public enum ReCouponFlag {
    CONFLICT("0", "不叠加"),
    SUPERPOSE("1", "可任意叠加"),
    SELFRULE("2", "自定义规则"),

    ;

    public static final String DICTID = "100179";
    public static final String DICTNAME = "ReCouponFlag";
    public static final String DICTKIND = "2";
    public static final String DICTCOMMENT = "叠加标识";

    private final String dictValue;
    private final String dictComment;

    ReCouponFlag(String dictValue, String dictComment) {
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
