package com.youyu.cardequity.promotion.enums;

import com.youyu.cardequity.common.base.enums.CardequityEnum;
import lombok.Getter;

/**
 * @Auther: zjm
 * @Date: 2019-04-28
 * @Description:
 */
@Getter
public enum CouponIssueResultEnum implements CardequityEnum {

    ISSUED_SUCCESSED("1","成功"),

    ISSUED_FAILED("2","失败");



    private String code;

    private String msg;

    CouponIssueResultEnum(String code, String msg) {
        this.code = code;
        this.msg = msg;
    }
}
