package com.youyu.cardequity.promotion.enums.dict;

/**
 * Created by caiyi on 2018/12/20.
 */
public enum DiscountApplyStage {
    CONDITION("0", "适用算入门槛内商品"),
    ALL("1", "适用券范围内商品"),
    ;

    public static final String DICTID = "100179";
    public static final String DICTNAME = "DiscountApplyStage";
    public static final String DICTKIND = "2";
    public static final String DICTCOMMENT = "折扣使用策略";

    private final String dictValue;
    private final String dictComment;

    DiscountApplyStage(String dictValue, String dictComment) {
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
