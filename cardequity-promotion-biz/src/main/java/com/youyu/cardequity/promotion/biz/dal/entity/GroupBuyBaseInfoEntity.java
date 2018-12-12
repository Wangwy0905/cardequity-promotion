package com.youyu.cardequity.promotion.biz.dal.entity;

import java.math.BigDecimal;
import java.time.LocalDate;
import javax.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Table(name = "TB_GROUP_BUY_BASE_INFO")
public class GroupBuyBaseInfoEntity extends com.youyu.common.entity.BaseEntity<String> {
    /**
     * 编号:
     */
    @Id
    @Column(name = "UUID")
    private String uuid;

    /**
     * 团购名称:
     */
    @Column(name = "GROUP_NAME")
    private String groupName;

    /**
     * 成团人数:为0即时生效
     */
    @Column(name = "GROUP_MIN_ACC")
    private String groupMinAcc;

    /**
     * 专属商品集合:只有这些买入商品才提供优惠，但是具体优惠需要通过阶梯表获取，为*标识所有
     */
    @Column(name = "PRODUCT_SET")
    private String productSet;

    /**
     * 专属商品组集合:只有这些买入商品组的商品才提供优惠，为*标识所有
     */
    @Column(name = "PRODUCT_GROUP_SET")
    private String productGroupSet;

    /**
     * 专属委托方式集合:只有指定的委托方式去买才提供优惠。如可以配置值允许移动端参与，为*标识所有
     */
    @Column(name = "ENTRUST_WAY_SET")
    private String entrustWaySet;

    /**
     * 团购开始日:到分秒级别
     */
    @Column(name = "BEGIN_DATE")
    private LocalDate beginDate;

    /**
     * 团购结束日:
     */
    @Column(name = "END_DATE")
    private LocalDate endDate;

    /**
     * 开团持续时间:控制开团方式为“个人私团”时的开团有效时间
     */
    @Column(name = "TERM_NUM")
    private Integer termNum;

    /**
     * 参与团购商品数量:如控制前一百件有优惠等
     */
    @Column(name = "PRODUCT_NUM")
    private Integer productNum;

    /**
     * 允许每人团购数量:如控制是否一人只允许参与一次，或者每次团购只允许买一件等
     */
    @Column(name = "PER_ACC_MAX_NUM")
    private Integer perAccMaxNum;

    /**
     * 团人数上限:如果开团方式为1-个人私团时，每个团的最大人数
     */
    @Column(name = "SUB_GROUP_MAX_ACC")
    private Integer subGroupMaxAcc;

    /**
     * 团人数下限:最少多少人成团
     */
    @Column(name = "SUB_GROUP_MIN_ACC")
    private Integer subGroupMinAcc;

    /**
     * 团购优惠方式:0-直减金额 1-折扣 2-指定价
     */
    @Column(name = "GROUP_PROFIT_TYPE")
    private String groupProfitType;

    /**
     * 团购优惠值:可以设置折扣值、直减金额、价格，如果指定一商品编号时可以设置价格
     */
    @Column(name = "PROFIT_VALUE")
    private BigDecimal profitValue;

    /**
     * 关联活动Id:关联什么活动
     */
    @Column(name = "JOIN_ACTIVITY_ID")
    private String joinActivityId;

    /**
     * 叠加码:定义为8位码。相同标识码可叠加，多个以逗号相隔(设置该券时，应向操作员自动展示可叠加券列表)，为空代表所有
     */
    @Column(name = "RE_COUPON_CODE")
    private String reCouponCode;

    /**
     * 叠加标志:0-不可叠加 1-可叠加 2-自定义（建议规则简单点，不采用该值）
     */
    @Column(name = "RE_COUPON_FLAG")
    private String reCouponFlag;

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