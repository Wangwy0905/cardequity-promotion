package com.youyu.cardequity.promotion.enums.dict;

/**
 * Created by caiyi on 2018/12/17.
 */
public enum StageCouponUseRule {
    SIMPLE("0", "保守策略"),
    OPTIMAL("1", "最优策略"),
    ;

    public static final String DICTID = "100179";
    public static final String DICTNAME = "StageCouponUseStrategy";
    public static final String DICTKIND = "2";
    public static final String DICTCOMMENT = "多张有门槛同种券使用规则";

    private final String dictValue;
    private final String dictComment;

    StageCouponUseRule(String dictValue, String dictComment) {
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
