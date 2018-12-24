package com.youyu.cardequity.promotion.vo.rsp;

import com.youyu.cardequity.promotion.dto.ActivityStageCouponDto;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

/**
 * Created by caiyi on 2018/12/11.
 */
@Getter
@Setter
public class ActivityDefineRsp {
    @ApiModelProperty(value = "活动编号:")
    private String uuid;

    @ApiModelProperty(value = "活动类型:0-限额任选 1-折扣 2-优惠价（只针对某一商品和折扣类型一样可以互相转换） 3-现金立减")
    private String activityCouponType;

    @ApiModelProperty(value = "活动名称:")
    private String activityName;

    @ApiModelProperty(value = "活动标签:用于前端展示：促销、秒杀")
    private String activityLable;

    @ApiModelProperty(value = "活动短描:用于前端展示：如任选3件99元")
    private String activityShortDesc;

    @ApiModelProperty(value = "专属商品集合:订单涉及门槛属性：只有这些买入商品才提供优惠，但是具体优惠需要通过阶梯表获取，为*标识所有，最大允许20个商品编号，超过20个通过商品组进行控制")
    private String productSet;

    @ApiModelProperty(value = "专属商品组集合:订单涉及门槛属性：只有这些买入商品组的商品才提供优惠，为*标识所有")
    private String productGroupSet;

    @ApiModelProperty(value = "专属客户类型集合:订单涉及门槛属性：只有这些这些客户类型才提供优惠。多种客户类型用逗号相隔，为*标识所有")
    private String clientTypeSet;

    @ApiModelProperty(value = "专属供应商集合:订单涉及门槛属性：只有这些这些供应商提供的产品才提供优惠。")
    private String sourceSet;

    @ApiModelProperty(value = "专属委托方式集合:订单涉及门槛属性：只有指定的委托方式去买才提供优惠。如可以配置值允许移动端参与，为*标识所有")
    private String entrustWaySet;

    @ApiModelProperty(value = "专属银行卡集合:订单涉及门槛属性：只有这些这些银行卡买的才提供优惠。")
    private String bankCodeSet;

    @ApiModelProperty(value = "专属支付方式集合:订单涉及门槛属性：只有指定的支付方式去买才提供优惠。如可以配置值允许信用卡参与，为*标识所有")
    private String payTypeSet;

    @ApiModelProperty(value = "优惠开始日:到分秒级别")
    private LocalDate allowUseBeginDate;

    @ApiModelProperty(value = "优惠结束日:")
    private LocalDate allowUseEndDate;

    @ApiModelProperty(value = "优惠值:如果是按阶梯进行“现金立减”该值无效，如果是按阶梯进行“折扣”该值无效，如果是阶梯“优惠价”该值无效，如果是阶梯“限额任选”该值无效，是以最终的阶梯中优惠值覆盖；如果ActivityType=1填折扣值(0-1]")
    private BigDecimal profitValue;

    @ApiModelProperty(value = "叠加码:定义为8位码。相同标识码可叠加，多个以逗号相隔(设置该券时，应向操作员自动展示可叠加券列表)，为空代表所有")
    private String reCouponCode;

    @ApiModelProperty(value = "叠加标志:0-不可叠加 1-可叠加 2-自定义（建议规则简单点，不采用该值）")
    private String reCouponFlag;

    @ApiModelProperty(value = "是否会员专属:")
    private String isAboutMember;

    @ApiModelProperty(value = "活动的门槛阶梯信息")
    private List<ActivityStageCouponDto> activityStageCouponDtoList;
}
