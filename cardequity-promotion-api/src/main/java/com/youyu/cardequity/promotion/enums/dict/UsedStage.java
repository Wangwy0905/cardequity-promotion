package com.youyu.cardequity.promotion.enums.dict;

/**
 * Created by caiyi on 2019/1/2.
 */
public enum UsedStage {
    AfterPay("0", "付款"),
    Confirm("1", "确认收货"),
    BeforeOrder("2", "选购"),
    Register("3", "注册"),
    Recommend("4", "推荐"),
    Share("5", "分享"),
    Other("6", "其他"),
  ;

    public static final String DICTID = "100179";
    public static final String DICTNAME = "UsedStage";
    public static final String DICTKIND = "2";
    public static final String DICTCOMMENT = "使用阶段";

    private final String dictValue;
    private final String dictComment;

    UsedStage(String dictValue, String dictComment) {
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
