package com.youyu.cardequity.promotion.biz.enums;

import com.youyu.cardequity.common.base.enums.CardequityEnum;
import com.youyu.cardequity.promotion.biz.dal.entity.ProductCouponEntity;
import lombok.Getter;

import static com.youyu.cardequity.common.base.util.EnumUtil.getCardequityEnum;

@Getter
public enum ProductCouponGetTypeEnum implements CardequityEnum {

    BACKEND_ISSUE("0", "后台发放") {
        @Override
        public boolean isUserGet() {
            return false;
        }
    },
    USER_GET("1", "用户领取") {
        @Override
        public void checkAllowGetDate(ProductCouponEntity productCouponEntity) {
            CouponValidTimeTypeEnum couponValidTimeTypeEnum = getCardequityEnum(CouponValidTimeTypeEnum.class, productCouponEntity.getValidTimeType());
            couponValidTimeTypeEnum.checkAllowGetDate(productCouponEntity);
        }
    },
    GRANT("2", "平台发放") {
        @Override
        public boolean isUserGet() {
            return false;
        }
    };

    private String code;

    private String msg;

    ProductCouponGetTypeEnum(String code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    public boolean isUserGet() {
        return true;
    }

    /**
     * 检验商品优惠券领取时间
     *
     * @param productCouponEntity
     */
    public void checkAllowGetDate(ProductCouponEntity productCouponEntity) {

    }
}
