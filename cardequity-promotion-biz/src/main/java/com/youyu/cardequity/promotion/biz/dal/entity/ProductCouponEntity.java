package com.youyu.cardequity.promotion.biz.dal.entity;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import javax.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Table(name = "TB_PRODUCT_COUPON")
public class ProductCouponEntity extends com.youyu.common.entity.BaseEntity<String> {
    /**
     * 优惠券编号:
     */
    @Id
    @Column(name = "UUID")
    private String uuid;

    /**
     * 优惠策略类型:0-折扣券(该表不会出现) 1-阶梯优惠券(满多少减多少)  2-定额优惠券（该券无阶梯优惠固定金额）
     */
    @Column(name = "COUPON_STRATEGY_TYPE")
    private String couponStrategyType;

    /**
     * 类型:0-红包 1-优惠券 2-运费券
     */
    @Column(name = "COUPON_TYPE")
    private String couponType;

    /**
     * 优惠名称:
     */
    @Column(name = "COUPON_NAME")
    private String couponName;

    /**
     * 优惠标签:标签：满返券、促销等
     */
    @Column(name = "COUPON_LABLE")
    private String couponLable;

    /**
     * 优惠短描:如满3件减20
     */
    @Column(name = "COUPON_SHORT_DESC")
    private String couponShortDesc;

    /**
     * 级别：0-自定义 1-全局
     */
    @Column(name = "COUPON_LEVEL")
    private String couponLevel;

    /**
     * 优惠说明
     */
    @Column(name = "COUPON_DESC")
    private String couponDesc;


    /**
     * 专属客户类型集合:订单涉及门槛属性：只有这些这些客户类型才提供优惠。多种客户类型用逗号相隔，为*标识所有
     */
    @Column(name = "CLIENT_TYPE_SET")
    private String clientTypeSet;

    /**
     * 专属供应商集合:订单涉及门槛属性：只有这些这些供应商提供的产品才提供优惠。
     */
    @Column(name = "SOURCE_SET")
    private String sourceSet;

    /**
     * 专属委托方式集合:订单涉及门槛属性：只有指定的委托方式去买才提供优惠。如可以配置值允许移动端参与，为*标识所有
     */
    @Column(name = "ENTRUST_WAY_SET")
    private String entrustWaySet;

    /**
     * 专属银行卡集合:订单涉及门槛属性：只有这些这些银行卡买的才提供优惠。
     */
    @Column(name = "BANK_CODE_SET")
    private String bankCodeSet;

    /**
     * 专属支付方式集合:订单涉及门槛属性：只有指定的支付方式去买才提供优惠。如可以配置值允许信用卡参与，为*标识所有
     */
    @Column(name = "PAY_TYPE_SET")
    private String payTypeSet;

    /**
     * 优惠开始日:到分秒级别
     */
    @Column(name = "ALLOW_USE_BEGIN_DATE")
    private LocalDateTime allowUseBeginDate;

    /**
     * 优惠结束日:
     */
    @Column(name = "ALLOW_USE_END_DATE")
    private LocalDateTime allowUseEndDate;

    /**
     * 领取开始日:到分秒级别
     */
    @Column(name = "ALLOW_GET_BEGIN_DATE")
    private LocalDateTime allowGetBeginDate;

    /**
     * 领取结束日:
     */
    @Column(name = "ALLOW_GET_END_DATE")
    private LocalDateTime allowGetEndDate;

    /**
     * 有效期限:以天为单位
     */
    @Column(name = "VAL_ID_TERM")
    private Integer valIdTerm;

    /**
     * 有效期限是否控制在不超过优惠结束日:0-否:有效结束日=实际领取日+期限 1-是：有效结束日=min(优惠结束日,(实际领取日+期限))
     */
    @Column(name = "USE_GE_END_DATE_FLAG")
    private String useGeEndDateFlag;

    /**
     * 优惠值:如果是阶梯或随机的填0，存折扣、金额
     */
    @Column(name = "PROFIT_VALUE")
    private BigDecimal profitValue;

    /**
     * 积分兑换额度:允许兑换该券的积分额度；积分不能兑换填99999999
     */
    @Column(name = "EXCHANGE_BY_POINT_VOL")
    private BigDecimal exchangeByPointVol;

    /**
     * 关联活动Id:多个券可用关联一个活动(组成活动大礼包)，但是一个券不能关联多个活动，只能再定义一张券：一般不设置获取规则，获取规则直接由活动进行自动发放
     */
    @Column(name = "JOIN_ACTIVITY_ID")
    private String joinActivityId;

    /**
     * 使用阶段:0-付后 1-确认收货后
     */
    @Column(name = "USED_STAGE")
    private String usedStage;

    /**
     * 获取阶段:0-付后 1-即时 2-确认收货后 3-注册 4-推荐 5-分享 6-平台指定发放
     */
    @Column(name = "GET_STAGE")
    private String getStage;

    /**
     * 领取方式:0-自动 1-手动 
     */
    @Column(name = "GET_TYPE")
    private String getType;

    /**
     * 备注
     */
    @Column(name = "REMARK")
    private String remark;

    /**
     * 叠加标志:0-不可叠加 1-可叠加 2-自定义（建议规则简单点，不采用该值）
     */
    @Column(name = "RE_COUPON_FLAG")
    private String reCouponFlag;


    /**
     * 适用商品类型:0-自动义商品范围 1-全部
     */
    @Column(name = "APPLY_PRODUCT_FLAG")
    private String applyProductFlag;

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