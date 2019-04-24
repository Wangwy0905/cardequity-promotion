package com.youyu.cardequity.promotion.enums;

import com.youyu.cardequity.common.base.enums.CardequityEnum;
import lombok.Getter;

@Getter
public enum CouponIssueVisibleEnum implements CardequityEnum {

    VISIBLE("0", "可见(上架)"),
    INVISIBLE("1", "不可见(下架)");

    private String code;

    private String msg;

    CouponIssueVisibleEnum(String code, String msg) {
        this.code = code;
        this.msg = msg;
    }
}
