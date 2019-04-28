package com.youyu.cardequity.promotion.biz.enums;

import com.youyu.cardequity.common.base.enums.CardequityEnum;
import lombok.Getter;

@Getter
public enum ProductCouponStatusEnum implements CardequityEnum {

    VISIBLE("1", "可见(上架)"),
    INVISIBLE("0", "不可见(下架)") {
        @Override
        public boolean isVisible() {
            return false;
        }
    };

    private String code;

    private String msg;

    ProductCouponStatusEnum(String code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    public boolean isVisible() {
        return true;
    }
}
