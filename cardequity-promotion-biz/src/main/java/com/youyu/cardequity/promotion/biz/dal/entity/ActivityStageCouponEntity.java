package com.youyu.cardequity.promotion.biz.dal.entity;

import java.math.BigDecimal;
import javax.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Table(name = "TB_ACTIVITY_STAGE_COUPON")
public class ActivityStageCouponEntity extends com.youyu.common.entity.BaseEntity<String> {
    /**
     * 编号:
     */
    @Id
    @Column(name = "UUID")
    private String uuid;

    /**
     * 活动阶段短描:用于前端展示：如任选3件99元、满200减50，会覆盖ActivityProfit.ActivityShortDesc
     */
    @Column(name = "ACTIVITY_SHORT_DESC")
    private String activityShortDesc;

    /**
     * 活动编号:
     */
    @Column(name = "ACTIVITY_ID")
    private String activityId;

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
     * 优惠值:使用时覆盖ActivityProfit中值起效
     */
    @Column(name = "PROFIT_VALUE")
    private BigDecimal profitValue;

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