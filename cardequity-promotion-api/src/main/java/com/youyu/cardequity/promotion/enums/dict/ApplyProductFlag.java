package com.youyu.cardequity.promotion.enums.dict;

/**
 * Created by caiyi on 2018/12/20.
 */
public enum ApplyProductFlag {
    APPOINTPRODUCT("0", "自定义"),
    ALL("1", "全部")

    ;

    public static final String DICTID = "100179";
    public static final String DICTNAME = "ApplyProductFlag";
    public static final String DICTKIND = "2";
    public static final String DICTCOMMENT = "适用商品标识";

    private final String dictValue;
    private final String dictComment;

    ApplyProductFlag(String dictValue, String dictComment) {
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
