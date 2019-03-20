package com.youyu.cardequity.promotion.enums.dict;

/**
 * Created by caiyi on 2018/12/20.
 */
public enum StageCouponAndProductComp {
    SIMPLE("0", "保守策略"),
    OPTIMAL("1", "最优策略"),
    FAST("2", "速算策略"),
    ;

    public static final String DICTID = "100179";
    public static final String DICTNAME = "StageCouponAndProductComp";
    public static final String DICTKIND = "2";
    public static final String DICTCOMMENT = "优惠券算法";

    private final String dictValue;
    private final String dictComment;

    StageCouponAndProductComp(String dictValue, String dictComment) {
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
