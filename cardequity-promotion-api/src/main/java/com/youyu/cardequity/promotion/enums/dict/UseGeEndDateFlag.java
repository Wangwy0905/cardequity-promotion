package com.youyu.cardequity.promotion.enums.dict;

/**
 * Created by caiyi on 2018/12/13.
 */
public enum UseGeEndDateFlag {

    NO("0", "否:有效结束日=实际领取日+期限"),
    YES("1", "是:有效结束日=min(优惠结束日,(实际领取日+期限))"),
    ;

    public static final String DICTID = "100179";
    public static final String DICTNAME = "UseGeEndDateFlag";
    public static final String DICTKIND = "2";
    public static final String DICTCOMMENT = "有效期限是否控制在不超过优惠结束日:0-否:有效结束日=实际领取日+期限 1-是：有效结束日=min(优惠结束日,(实际领取日+期限))";

    private final String dictValue;
    private final String dictComment;

    UseGeEndDateFlag(String dictValue, String dictComment) {
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
