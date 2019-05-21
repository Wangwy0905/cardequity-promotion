package com.youyu.cardequity.promotion.biz.enums;


import com.youyu.cardequity.common.base.enums.CardequityEnum;
import com.youyu.cardequity.promotion.biz.dal.entity.ProductCouponEntity;
import lombok.Getter;

import java.util.concurrent.CompletionService;
import java.util.concurrent.ExecutorCompletionService;

import static com.youyu.cardequity.promotion.enums.dict.ApplyProductFlag.ALL;
import static com.youyu.cardequity.promotion.enums.dict.CouponActivityLevel.GLOBAL;

/**
 * @author panqingqing
 * @version v1.0
 * @date 2019年5月13日 10:00:00
 * @work 优惠券类型枚举
 */
@Getter
public enum CouponTypeEnum implements CardequityEnum {

    MONEY_BAG("0", "红包") {
        @Override
        public void setApplyProductFlagCouponLevel(ProductCouponEntity productCouponEntity) {

        }
    },
    COUPON("1", "优惠券") {
        @Override
        public void setApplyProductFlagCouponLevel(ProductCouponEntity productCouponEntity) {

        }
    },
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

    /**
     * 设置ApplyProductFlag和CouponLevel属性
     *
     * @param productCouponEntity
     */
    public void setApplyProductFlagCouponLevel(ProductCouponEntity productCouponEntity) {
        productCouponEntity.setCouponLevel(GLOBAL.getDictValue());
        productCouponEntity.setApplyProductFlag(ALL.getDictValue());

    }
}
