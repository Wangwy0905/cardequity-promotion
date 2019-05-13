package com.youyu.cardequity.promotion.enums;

import lombok.Getter;

/**
 * @Auther: zjm
 * @Date: 2019-05-12
 * @Description: 用户的类型，用于优惠券后台发放时身份的校验,code中的-1指的是注册，0指的是注册已过期
 * {@link CouponGetRestrictEnum}
 */
@Getter
public class UserType {
    public static final String REGISTER = "-1";
    public static final String NON_REGISTER = "0";

    public static final String GIFT_MEMBER = "12";

    public static final String MEMBER = "11";

    @Getter
    public enum UserTypeEnum {

        REGISTER_NORMAL("-1,10", "注册 普通（非会员）") {
            @Override
            public boolean isRegister() {
                return true;
            }
        },

        REGISTER_MEMBER("-1,11", "注册 会员") {
            @Override
            public boolean isRegister() {
                return true;
            }

            @Override
            public boolean isMember() {
                return true;
            }

            @Override
            public boolean isRegisterMember() {
                return true;
            }


        },

        NON_REGISTER_NORMAL("0,10", "非注册 正式"),


        NON_REGISTER_MEMBER("0,11", "非注册 会员") {
            @Override
            public boolean isMember() {
                return true;
            }
        };


        UserTypeEnum(String code, String desc) {
            this.code = code;
            this.desc = desc;
        }

        private String code;
        private String desc;


        public boolean isRegister() {
            return false;
        }

        public boolean isMember() {
            return false;
        }

        public boolean isRegisterMember() {
            return false;
        }

        public static UserTypeEnum valuesOf(String typeSet) {
            for (UserTypeEnum var : values()) {
                if (var.getCode().equals(typeSet)) {
                    return var;
                }
            }
            throw new IllegalArgumentException("No matching constant for typeSet:[" + typeSet + "]");

        }
    }

    public static UserTypeEnum valuesOf(String typeSet) {
        return UserTypeEnum.valuesOf(typeSet);
    }
}
