package com.youyu.cardequity.promotion.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

import com.youyu.common.dto.IBaseDto;
import lombok.Data;
import io.swagger.annotations.ApiModelProperty;
import io.swagger.annotations.ApiModel;


/**
 *  代码生成器
 *
 * @author 技术平台
 * @date 2018-12-07
 */
@Data
@ApiModel
public class ActivityProfitDto implements IBaseDto<String>{

    @ApiModelProperty(value = "活动编号:")
    private String uuid;

    @ApiModelProperty(value = "活动类型:0-限额任选 1-折扣 2-优惠价（只针对某一商品和折扣类型一样可以互相转换） 3-现金立减")
    private String activityCouponType;

    @ApiModelProperty(value = "活动名称:")
    private String activityName;

    @ApiModelProperty(value = "优惠标签:标签：满返券、促销等")
    private CouponAndActivityLabelDto labelDto ;

    @ApiModelProperty(value = "活动短描:用于前端展示：如任选3件99元")
    private String activityShortDesc;

    @ApiModelProperty(value = "级别：0-自动义 1-全局")
    private String activityLevel;

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
    private LocalDateTime allowUseBeginDate;

    @ApiModelProperty(value = "优惠结束日:")
    private LocalDateTime allowUseEndDate;

    @ApiModelProperty(value = "优惠值:如果是按阶梯进行“现金立减”该值无效，如果是按阶梯进行“折扣”该值无效，如果是阶梯“优惠价”该值无效，如果是阶梯“限额任选”该值无效，是以最终的阶梯中优惠值覆盖；如果ActivityType=1填折扣值(0-1]")
    private BigDecimal profitValue;

    @ApiModelProperty(value = "备注")
    private String remark;

    @ApiModelProperty(value = "叠加标志:0-不可叠加 1-可叠加 2-自定义（建议规则简单点，不采用该值）")
    private String reCouponFlag;

    @ApiModelProperty(value = "适用商品类型:0-自动义商品范围 1-全部")
    private String applyProductFlag;

    @Override
    @ApiModelProperty(value = "活动编号:")
    public String getId() {
        return uuid;
    }

    @Override
    public void setId(String id) {
        this.uuid = id;
    }
}

