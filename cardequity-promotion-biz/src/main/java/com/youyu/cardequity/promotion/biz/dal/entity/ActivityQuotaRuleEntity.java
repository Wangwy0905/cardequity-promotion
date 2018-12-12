package com.youyu.cardequity.promotion.biz.dal.entity;

import java.math.BigDecimal;
import javax.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Table(name = "TB_ACTIVITY_QUOTA_RULE")
public class ActivityQuotaRuleEntity extends com.youyu.common.entity.BaseEntity<String> {
    /**
     * 活动编号:
     */
    @Id
    @Column(name = "ACTIVITY_ID")
    private String activityId;

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
     * 活动最大优惠金额(资金池数量):优惠券金额池，控制金额不能超出池子金额
     */
    @Column(name = "MAX_AMOUNT")
    private BigDecimal maxAmount;

    /**
     * 每笔最大数量:如每单限购10件
     */
    @Column(name = "PER_MAX_COUNT")
    private BigDecimal perMaxCount;

    /**
     * 每客每天最大数量:如每人每天限量10件
     */
    @Column(name = "PER_DATE_AND_ACC_MAX_COUNT")
    private BigDecimal perDateAndAccMaxCount;

    /**
     * 每天最大数量:如每天限量1000件
     */
    @Column(name = "PER_DATE_MAX_COUNT")
    private BigDecimal perDateMaxCount;

    /**
     * 每客最大数量:如每人限量10件
     */
    @Column(name = "PERSON_MAX_COUNT")
    private BigDecimal personMaxCount;

    /**
     * 活动最大数量:做活动的商品数量
     */
    @Column(name = "MAX_COUNT")
    private BigDecimal maxCount;

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
        return activityId;
    }

    @Override
    public void setId(String activityId) {
        this.activityId = activityId;
    }
}