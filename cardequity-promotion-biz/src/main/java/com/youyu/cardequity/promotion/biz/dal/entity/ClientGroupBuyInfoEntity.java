package com.youyu.cardequity.promotion.biz.dal.entity;

import java.math.BigDecimal;
import java.time.LocalDate;
import javax.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Table(name = "TB_CLIENT_GROUP_BUY_INFO")
public class ClientGroupBuyInfoEntity extends com.youyu.common.entity.BaseEntity<String> {
    /**
     * 团购编号:
     */
    @Id
    @Column(name = "UUID")
    private String uuid;

    /**
     * 客户编号:
     */
    @Id
    @Column(name = "CLIENT_ID")
    private String clientId;

    /**
     * 开团码:
     */
    @Id
    @Column(name = "OPEN_GROUP_CODE")
    private String openGroupCode;

    /**
     * 银行账号:
     */
    @Column(name = "BANK_ACCOUNT_ID")
    private String bankAccountId;

    /**
     * 委托方式:
     */
    @Column(name = "ENTRUST_WAY")
    private String entrustWay;

    /**
     * 状态:0-团购中 1-团购失败 2-返现中 3-成功
     */
    @Column(name = "STATUS")
    private String status;

    /**
     * 订单编号:只有这些买入商品组的商品才提供优惠，为*标识所有
     */
    @Column(name = "ORDER_ID")
    private String orderId;

    /**
     * 商品编号:只有这些买入商品组的商品才提供优惠，为*标识所有
     */
    @Column(name = "PRODUCT_ID")
    private String productId;

    /**
     * SkuId:网易:ApiSkuTo.Id，对于苏宁填子编号
     */
    @Column(name = "SKU_ID")
    private String skuId;

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
     * 团购开始时间:到分秒级别
     */
    @Column(name = "BEGIN_DATE")
    private LocalDate beginDate;

    /**
     * 团购结束时间:
     */
    @Column(name = "END_DATE")
    private LocalDate endDate;

    /**
     * 团购价:
     */
    @Column(name = "GROUP_PRICE")
    private BigDecimal groupPrice;

    /**
     * 支付价格:
     */
    @Column(name = "REAL_PRICE")
    private BigDecimal realPrice;

    /**
     * 数量:
     */
    @Column(name = "BUY_COUNT")
    private Integer buyCount;

    /**
     * 团购返现金额:返现成功后填入
     */
    @Column(name = "BACK_AMOUNT")
    private BigDecimal backAmount;

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