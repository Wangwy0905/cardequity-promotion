package com.youyu.cardequity.promotion.biz.dal.entity;

import javax.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Table(name = "TB_COUPON_REF_PRODUCT")
public class CouponRefProductEntity extends com.youyu.common.entity.BaseEntity<String> {
    /**
     * 活动编号:
     */
    @Id
    @Column(name = "UUID")
    private String uuid;

    /**
     * 活动编号:
     */
    @Column(name = "COUPON_ID")
    private String couponId;

    /**
     * 商品id:
     */
    @Column(name = "PRODUCT_ID")
    private String productId;

    /**
     * 子商品id:
     */
    @Column(name = "SKU_ID")
    private String skuId;

    /**
     * 备注:
     */
    @Column(name = "REMARK")
    private String remark;

    /**
     * 是否有效:
     */
    @Column(name = "IS_ENABLE")
    private String isEnable;

    @Override
    public String getId() {
        return uuid;
    }

    @Override
    public void setId(String uuid) {
        this.uuid = uuid;
    }
}