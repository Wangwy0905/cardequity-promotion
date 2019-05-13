package com.youyu.cardequity.promotion.biz.enums;


import com.youyu.cardequity.common.base.enums.CardequityEnum;
import lombok.Getter;

/**
 * @author panqingqing
 * @version v1.0
 * @date 2019年5月13日 10:00:00
 * @work 优惠券类型枚举
 */
@Getter
public enum CouponTypeEnum implements CardequityEnum {

    MONEY_BAG("0", "红包"),
    COUPON("1", "优惠券"),
    TRANSFER_FARE("2", "运费券") {
        @Override
        public boolean canGlobal() {
            return true;
        }
    },
    FREE_TRANSFER_FARE("3", "免邮券") {
        @Override
        public boolean canGlobal() {
            return true;
        }
    };

    private String code;

    private String msg;

    CouponTypeEnum(String code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    public boolean canGlobal() {
        return false;
    }
}
