package com.youyu.cardequity.promotion.enums.dict;

/**
 * 订单状态
 *
 * @author hanxiaorui
 * @date 2018年12月10日
 * @work 订单状态
 */
public enum CouponStatus {
    NORMAL("0", "正常"),
    USING("1", "使用中"),
    USED("2", "已使用"),

    ;

    public static final String DICTID = "100179";
    public static final String DICTNAME = "CouponStatus";
    public static final String DICTKIND = "2";
    public static final String DICTCOMMENT = "优惠券状态";

    private final String dictValue;
    private final String dictComment;

    CouponStatus(String dictValue, String dictComment) {
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
