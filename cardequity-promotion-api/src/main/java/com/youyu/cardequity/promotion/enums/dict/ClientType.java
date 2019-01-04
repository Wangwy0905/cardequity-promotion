package com.youyu.cardequity.promotion.enums.dict;

/**
 * Created by caiyi on 2019/1/3.
 */
public enum  ClientType {
    COMMON("10", "普通客户"),
    MEMBER("11", "会员"),

    ;

    public static final String DICTID = "100179";
    public static final String DICTNAME = "ClientType";
    public static final String DICTKIND = "2";
    public static final String DICTCOMMENT = "客户类型";

    private final String dictValue;
    private final String dictComment;

    ClientType(String dictValue, String dictComment) {
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
