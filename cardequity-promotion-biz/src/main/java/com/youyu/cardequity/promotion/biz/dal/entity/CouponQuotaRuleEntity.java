package com.youyu.cardequity.promotion.biz.dal.entity;

import java.math.BigDecimal;
import javax.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Table(name = "TB_COUPON_QUOTA_RULE")
public class CouponQuotaRuleEntity extends com.youyu.common.entity.BaseEntity<String> {
    /**
     * 优惠券编号:
     */
    @Id
    @Column(name = "COUPON_ID")
    private String couponId;

    /**
     * 每笔最大优惠额:每笔优惠池，主要多折扣券的总优惠金额做保护
     */
    @Column(name = "PER_MAX_AMOUNT")
    private BigDecimal perMaxAmount;

    /**
     * 每客每天最大优惠额:每天每客优惠池
     */
    @Column(name = "PER_DATE_AND_ACC_MAX_AMOUNT")
    private BigDecimal perDateAndAccMaxAmount;

    /**
     * 每天最大优惠额:每天优惠池
     */
    @Column(name = "PER_DATE_MAX_AMOUNT")
    private BigDecimal perDateMaxAmount;

    /**
     * 每客最大优惠额:每客优惠池
     */
    @Column(name = "PERSON_MAX_AMOUNT")
    private BigDecimal personMaxAmount;

    /**
     * 最大优惠金额(资金池数量):优惠券金额池，控制金额不能超出池子金额
     */
    @Column(name = "MAX_AMOUNT")
    private BigDecimal maxAmount;

    /**
     * 最大发放数量(券池数量):优惠券数量池
     */
    @Column(name = "MAX_COUNT")
    private Integer maxCount;

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
        return couponId;
    }

    @Override
    public void setId(String couponId) {
        this.couponId = couponId;
    }
}