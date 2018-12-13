package com.youyu.cardequity.promotion.biz.dal.entity;

import java.math.BigDecimal;
import java.time.LocalDate;
import javax.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Table(name = "TB_CLIENT_COUPON")
public class ClientCouponEntity extends com.youyu.common.entity.BaseEntity<String> {
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
     * 阶梯编号:
     */
    @Column(name = "STAGE_ID")
    private String stageId;

    /**
     * 客户号:
     */
    @Column(name = "CLIENT_ID")
    private String clientId;

    /**
     * 委托方式:通过什么方式获取
     */
    @Column(name = "ENTRUST_WAY")
    private String entrustWay;

    /**
     * 优惠金额:
     */
    @Column(name = "COUPON_AMOUT")
    private BigDecimal couponAmout;

    /**
     * 有效起始日:到时分秒
     */
    @Column(name = "VALID_START_DATE")
    private LocalDate validStartDate;

    /**
     * 有效结束日:到时分秒
     */
    @Column(name = "VALID_END_DATE")
    private LocalDate validEndDate;

    /**
     * 状态:0-正常 1-使用中 2-已使用
     */
    @Column(name = "STATUS")
    private String status;

    /**
     * 关联订单号:多次恢复可能涉及多个订单号时填最近一次订单号
     */
    @Column(name = "JOIN_ORDER_ID")
    private String joinOrderId;

    /**
     * 备注:
     */
    @Column(name = "REMARK")
    private String remark;

    /**
     * 使用日期
     */
    @Column(name = "BUSIN_DATE")
    private LocalDate BusinDate;


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