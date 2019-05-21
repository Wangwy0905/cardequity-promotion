package com.youyu.cardequity.promotion.biz.dal.entity;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Id;
import javax.persistence.Table;

@Getter
@Setter
@Table(name = "TB_COUPON_REF_PRODUCT")
//优惠活动定义适用商品表
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
     * 商品名称
     */
    @Column(name = "PRODUCT_NAME")
    private String productName;

    /**
     * 供应商
     */
    @Column(name = "SUPPLIER_NAME")
    private String supplierName;

    /**
     * 三级分类
     */
    @Column(name = "THIRD_CATEGORY_NAME")
    private String thirdCategoryName;

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