package com.youyu.cardequity.promotion.enums;

import lombok.Getter;

/**
 * @Auther: zjm
 * @Date: 2019-05-12
 * @Description: 优惠券限制的用户领取类型
 */
@Getter
public enum CouponGetRestrictEnum {
    ALL("*", "所有用户可领，不限制") {
        @Override
        public boolean canGet(UserType.UserTypeEnum userType) {
            return true;
        }
    },

    REGISTER_NORMAL("10", "注册") {
        @Override
        public boolean canGet(UserType.UserTypeEnum userType) {
            return userType.isRegister();
        }
    },

    REGISTER_MEMBER("10,11", "注册 会员") {
        @Override
        public boolean canGet(UserType.UserTypeEnum userType) {
            return userType.isRegisterMember();
        }
    },

    MEMBER("11", "会员") {
        @Override
        public boolean canGet(UserType.UserTypeEnum userType) {
            return userType.isMember();
        }
    };


    private String code;
    private String desc;

    CouponGetRestrictEnum(String code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    public abstract boolean canGet(UserType.UserTypeEnum userType);

    public static CouponGetRestrictEnum valuesOf(String typeSet) {
        for (CouponGetRestrictEnum var : values()) {
            if (var.getCode().equals(typeSet)) {
                return var;
            }
        }
        throw new IllegalArgumentException("No matching constant for typeSet:[" + typeSet + "]");

    }
}