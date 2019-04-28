package com.youyu.cardequity.promotion.enums;

import com.youyu.cardequity.common.base.enums.CardequityEnum;
import lombok.Getter;

/**
 * @author panqingqing
 * @version v1.0
 * @date 2019年04月25日 15:00:00
 * @work 优惠券发放状态枚举
 */
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
