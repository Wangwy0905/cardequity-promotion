package com.youyu.cardequity.promotion.enums;

import com.youyu.cardequity.common.base.enums.CardequityEnum;
import lombok.Getter;

@Getter
public enum CouponIssueStatusEnum implements CardequityEnum {

    NOT_ISSUE("1", "未发放"),
    ISSUING("2", "发放中"),
    ISSUED("3", "已完成");

    private String code;

    private String msg;

    CouponIssueStatusEnum(String code, String msg) {
        this.code = code;
        this.msg = msg;
    }
}
