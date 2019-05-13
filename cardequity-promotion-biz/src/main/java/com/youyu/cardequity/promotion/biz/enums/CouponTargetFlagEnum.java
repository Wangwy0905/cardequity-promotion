package com.youyu.cardequity.promotion.biz.enums;

import com.youyu.cardequity.common.base.enums.CardequityEnum;
import com.youyu.cardequity.promotion.biz.dal.entity.ProductCouponEntity;
import com.youyu.cardequity.promotion.dto.req.AddCouponReq2;
import com.youyu.cardequity.promotion.enums.CouponGetRestrictEnum;
import com.youyu.cardequity.promotion.enums.dict.UsedStage;
import lombok.Getter;

/**
 * @author panqingqing
 * @version v1.0
 * @date 2019年5月13日 10:00:00
 * @work 优惠券领取对象类型枚举 与 {@link CouponGetRestrictEnum 有关联,不能轻易修改}
 */
@Getter
public enum CouponTargetFlagEnum implements CardequityEnum {

    ALL_USER("0", "全部用户") {
        @Override
        public void setClientTypeSet(ProductCouponEntity productCouponEntity, AddCouponReq2 addCouponReq2) {
            productCouponEntity.setClientTypeSet(CouponGetRestrictEnum.ALL.getCode());
            productCouponEntity.setGetStage(UsedStage.Other.getDictValue());
        }
    },
    REGISTRY_USER("1", "注册用户") {
        @Override
        public void setClientTypeSet(ProductCouponEntity productCouponEntity, AddCouponReq2 addCouponReq2) {
            productCouponEntity.setClientTypeSet(CouponGetRestrictEnum.REGISTER_NORMAL.getCode());
            productCouponEntity.setGetStage(UsedStage.Register.getDictValue());
        }
    },
    MEMBER_USER("2", "会员") {
        @Override
        public void setClientTypeSet(ProductCouponEntity productCouponEntity, AddCouponReq2 addCouponReq2) {
            productCouponEntity.setClientTypeSet(CouponGetRestrictEnum.MEMBER.getCode());
            productCouponEntity.setGetStage(UsedStage.Other.getDictValue());
        }
    };

    private String code;

    private String msg;

    CouponTargetFlagEnum(String code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    /**
     * 设置优惠券用户类型
     *
     * @param productCouponEntity
     * @param addCouponReq2
     */
    public abstract void setClientTypeSet(ProductCouponEntity productCouponEntity, AddCouponReq2 addCouponReq2);
}
