package com.youyu.cardequity.promotion.enums.dict;

/**
 * Created by caiyi on 2018/12/14.
 */
public enum ConflictFlag {
    SUPERPOSE("0", "叠加"),
    CONFLICT("1", "冲突"),

    ;

    public static final String DICTID = "100179";
    public static final String DICTNAME = "ConflictFlag";
    public static final String DICTKIND = "2";
    public static final String DICTCOMMENT = "冲突标识";

    private final String dictValue;
    private final String dictComment;

    ConflictFlag(String dictValue, String dictComment) {
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
