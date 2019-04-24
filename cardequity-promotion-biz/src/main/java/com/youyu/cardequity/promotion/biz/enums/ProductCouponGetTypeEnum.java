package com.youyu.cardequity.promotion.biz.enums;

import com.youyu.cardequity.common.base.enums.CardequityEnum;
import lombok.Getter;

@Getter
public enum ProductCouponGetTypeEnum implements CardequityEnum {

    AUTO("0", "自动") {
        @Override
        public boolean isHanld() {
            return false;
        }
    },
    HANLD("1", "手动"),
    GRANT("2", "平台发放") {
        @Override
        public boolean isHanld() {
            return false;
        }
    };

    private String code;

    private String msg;

    ProductCouponGetTypeEnum(String code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    public boolean isHanld() {
        return true;
    }
}
