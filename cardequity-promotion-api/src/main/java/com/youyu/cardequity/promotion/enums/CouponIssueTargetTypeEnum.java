package com.youyu.cardequity.promotion.enums;

import com.youyu.cardequity.common.base.enums.CardequityEnum;
import lombok.Getter;

/**
 * @author panqingqing
 * @version v1.0
 * @date 2019年04月25日 15:00:00
 * @work 优惠券发放对象类型
 */
@Getter
public enum CouponIssueTargetTypeEnum implements CardequityEnum {

    CLIENT_ID("1", "用户id"),
    ACTIVITY_ID("2", "活动id");

    private String code;

    private String msg;

    CouponIssueTargetTypeEnum(String code, String msg) {
        this.code = code;
        this.msg = msg;
    }
}
