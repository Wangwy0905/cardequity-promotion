package com.youyu.cardequity.promotion.biz.dal.entity;

import java.math.BigDecimal;
import javax.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Table(name = "TB_COUPON_STAGE_USE_AND_GET_RULE")
public class CouponStageRuleEntity extends com.youyu.common.entity.BaseEntity<String> {
    /**
     * 阶段编号:
     */
    @Id
    @Column(name = "UUID")
    private String uuid;

    /**
     * 优惠券编号:
     */
    @Column(name = "COUPON_ID")
    private String couponId;

    /**
     * 优惠短描:如满3件减20，会覆盖ProductCoupon.CouponShortDesc
     */
    @Column(name = "COUPON_SHORT_DESC")
    private String couponShortDesc;

    /**
     * 门槛触发类型:0-按买入金额 1-按买入数量（应设置其中之一，如果第二件5折可在此设置）
     */
    @Column(name = "TRIGGER_BY_TYPE")
    private String triggerByType;

    /**
     * 值起始（不含）:没有阶梯填(0,999999999]
     */
    @Column(name = "BEGIN_VALUE")
    private BigDecimal beginValue;

    /**
     * 结束值（含）:最大值为999999999；
     */
    @Column(name = "END_VALUE")
    private BigDecimal endValue;

    /**
     * 优惠值:默认值等于ProductGroupCoupon中值，使用时覆盖ProductGroupCoupon中值起效，取值范围为ProductGroupCoupon中值
     */
    @Column(name = "COUPON_VALUE")
    private BigDecimal couponValue;

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