package com.youyu.cardequity.promotion.biz.dal.entity;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import javax.persistence.*;

import com.youyu.cardequity.common.base.converter.BeanPropertiesConverter;
import com.youyu.cardequity.promotion.dto.ActivityProfitDto;
import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.BeanUtils;

@Getter
@Setter
@Table(name = "TB_ACTIVITY_PROFIT")
public class ActivityProfitEntity extends com.youyu.common.entity.BaseEntity<String> {
    /**
     * 活动编号:
     */
    @Id
    @Column(name = "UUID")
    private String uuid;

    /**
     * 活动类型:0-限额任选 1-折扣 2-优惠价（只针对某一商品和折扣类型一样可以互相转换） 3-现金立减
     */
    @Column(name = "ACTIVITY_COUPON_TYPE")
    private String activityCouponType;

    /**
     * 活动名称:
     */
    @Column(name = "ACTIVITY_NAME")
    private String activityName;

    /**
     * 活动标签:用于前端展示：促销、秒杀
     */
    @Column(name = "ACTIVITY_LABLE")
    private String activityLable;

    /**
     * 活动短描:用于前端展示：如任选3件99元
     */
    @Column(name = "ACTIVITY_SHORT_DESC")
    private String activityShortDesc;

    /**
     * 专属商品集合:订单涉及门槛属性：只有这些买入商品才提供优惠，但是具体优惠需要通过阶梯表获取，为*标识所有，最大允许20个商品编号，超过20个通过商品组进行控制
     */
    @Column(name = "PRODUCT_SET")
    private String productSet;

    /**
     * 专属商品组集合:订单涉及门槛属性：只有这些买入商品组的商品才提供优惠，为*标识所有
     */
    @Column(name = "PRODUCT_GROUP_SET")
    private String productGroupSet;

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
    private LocalDate allowUseBeginDate;

    /**
     * 优惠结束日:
     */
    @Column(name = "ALLOW_USE_END_DATE")
    private LocalDate allowUseEndDate;

    /**
     * 优惠值:如果是按阶梯进行“现金立减”该值无效，如果是按阶梯进行“折扣”该值无效，如果是阶梯“优惠价”该值无效，如果是阶梯“限额任选”该值无效，是以最终的阶梯中优惠值覆盖；如果ActivityType=1填折扣值(0-1]
     */
    @Column(name = "PROFIT_VALUE")
    private BigDecimal profitValue;

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
