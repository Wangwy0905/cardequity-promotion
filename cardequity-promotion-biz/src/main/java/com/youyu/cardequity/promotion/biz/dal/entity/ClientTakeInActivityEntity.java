package com.youyu.cardequity.promotion.biz.dal.entity;

import java.math.BigDecimal;
import javax.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Table(name = "TB_CLIENT_TAKE_IN_ACTIVITY")
public class ClientTakeInActivityEntity extends com.youyu.common.entity.BaseEntity<String> {
    /**
     * 编号:
     */
    @Id
    @Column(name = "UUID")
    private String uuid;

    /**
     * 领取编号:
     */
    @Column(name = "ACTIVITY_ID")
    private String activityId;

    /**
     * 阶梯编号:
     */
    @Column(name = "STAGE_ID")
    private String stageId;

    /**
     * 订单号:
     */
    @Column(name = "ORDER_ID")
    private String orderId;

    /**
     * 客户号:
     */
    @Column(name = "CLIENT_ID")
    private String clientId;

    /**
     * 商品编号:
     */
    @Column(name = "PRODUCT_ID")
    private String productId;

    /**
     * SkuId:网易:ApiSkuTo.Id，对于苏宁填子编号
     */
    @Column(name = "SKU_ID")
    private String skuId;

    /**
     * 商品总额:优惠前总值
     */
    @Column(name = "PRODUCT_AMOUNT")
    private BigDecimal productAmount;

    /**
     * 数量:
     */
    @Column(name = "PRODUCT_COUNT")
    private BigDecimal productCount;

    /**
     * 优惠值:主要优惠金额、
     */
    @Column(name = "PROFIT_VALUE")
    private BigDecimal profitValue;

    /**
     * 业务代码:优惠券使用、券撤销
     */
    @Column(name = "BUSIN_CODE")
    private String businCode;

    /**
     * 参与状态:0-参与中 1-使用中 2-已使用
     */
    @Column(name = "STATUS")
    private String status;

    /**
     * 备注:
     */
    @Column(name = "REMARK")
    private String remark;

    /**
     * 是否有效:当恢复正常时设置为0
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