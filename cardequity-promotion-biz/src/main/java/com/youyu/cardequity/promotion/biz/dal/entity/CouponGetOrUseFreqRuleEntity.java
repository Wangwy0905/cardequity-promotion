package com.youyu.cardequity.promotion.biz.dal.entity;

import javax.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Table(name = "TB_COUPON_GET_OR_USE_FREQ_RULE")

public class CouponGetOrUseFreqRuleEntity extends com.youyu.common.entity.BaseEntity<String> {
    /**
     * 编号:
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
     * 操作方式:0-获取 1-使用
     */
    @Column(name = "OP_COUPON_TYPE")
    private String opCouponType;

    /**
     * 优惠阶梯编号:
     */
    @Column(name = "STAGE_ID")
    private String stageId;

    /**
     * 频率单位:0-天 1-周 2-月 3-年
     */
    @Column(name = "UNIT")
    private String unit;

    /**
     * 频率值:预留字段默认为1，暂不支持多周期频率
     */
    @Column(name = "VALUE")
    private Integer value;

    /**
     * 允许次数:设置时注意每天使用数>=ProductCoupon每次允许使用数；特殊值0代表任何时刻都只有且可以有一张有效的券（即客户每时刻可以且只能有一张有效券），此时Unit值无效
     */
    @Column(name = "ALLOW_COUNT")
    private Integer allowCount;

    /**
     * 客户获取总数/客户每次使用数:999999标识无限制
     */
    @Column(name = "PERSON_TOTAL_NUM")
    private Integer personTotalNum;

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