package com.youyu.cardequity.promotion.enums;

import com.youyu.cardequity.common.base.enums.CardequityEnum;
import lombok.Getter;

/**
 * @author panqingqing
 * @version v1.0
 * @date 2019年5月13日 10:00:00
 * @work 优惠券领取对象类型枚举
 */
@Getter
public enum ProductCouponClientTypeSetEnum implements CardequityEnum {

    ALL_USER("*", "所有用户"),
    REGISTRY_USER("10", "注册用户"),
    MEMBER_USER("11", "会员用户");

    private String code;

    private String msg;

    ProductCouponClientTypeSetEnum(String code, String msg) {
        this.code = code;
        this.msg = msg;
    }
}
