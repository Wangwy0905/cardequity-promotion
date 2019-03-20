package com.youyu.cardequity.promotion.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

import com.youyu.cardequity.promotion.enums.dict.CouponStatus;
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
public class ProductCouponDto implements IBaseDto<String>{

    public ProductCouponDto(){
        status= CouponStatus.NO.getDictValue();
    }

    @ApiModelProperty(value = "领取编号:")
    private String uuid;

    @ApiModelProperty(value = "优惠策略类型:0-折扣券 1-满减现金 2-无门槛现金券  3-随机  4-每满减现金")
    private String couponStrategyType;

    @ApiModelProperty(value = "类型:0-红包 1-消费券 2-运费券")
    private String couponType;

    @ApiModelProperty(value = "优惠名称:")
    private String couponName;

    @ApiModelProperty(value = "优惠标签:标签：满返券、促销等")
    private CouponAndActivityLabelDto labelDto ;

    @ApiModelProperty(value = "优惠短描:如满3件减20")
    private String couponShortDesc;

    @ApiModelProperty(value = "说明")
    private String couponDesc;

    @ApiModelProperty(value = "级别：0-小鱼券 1-大鱼券")
    private String couponLevel;

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

    @ApiModelProperty(value = "领取开始日:到分秒级别")
    private LocalDateTime allowGetBeginDate;

    @ApiModelProperty(value = "领取结束日:")
    private LocalDateTime allowGetEndDate;

    @ApiModelProperty(value = "有效期限:以天为单位")
    private Integer valIdTerm;

    @ApiModelProperty(value = "有效期限是否控制在不超过优惠结束日:0-否:有效结束日=实际领取日+期限 1-是：有效结束日=min(优惠结束日,(实际领取日+期限))")
    private String useGeEndDateFlag;

    @ApiModelProperty(value = "优惠值:如果是阶梯或随机的填0，存折扣、金额")
    private BigDecimal profitValue;

    @ApiModelProperty(value = "状态:0-下架 1-上架")
    private String status;


    @ApiModelProperty(value = "积分兑换额度:允许兑换该券的积分额度；积分不能兑换填99999999")
    private BigDecimal exchangeByPointVol;

    @ApiModelProperty(value = "关联活动Id:多个券可用关联一个活动(组成活动大礼包)，但是一个券不能关联多个活动，只能再定义一张券：一般不设置获取规则，获取规则直接由活动进行自动发放")
    private String joinActivityId;

    @ApiModelProperty(value = "使用阶段:0-付后 1-确认收货后")
    private String usedStage;

    @ApiModelProperty(value = "获取阶段:0-付后 1-即时 2-确认收货后 3-注册 4-推荐 5-分享 6-平台指定发放")
    private String getStage;

    @ApiModelProperty(value = "领取方式:0-自动 1-手动 ")
    private String getType;

    @ApiModelProperty(value = "备注")
    private String remark;

    @ApiModelProperty(value = "叠加标志:0-不可叠加 1-可叠加 2-自定义（建议规则简单点，不采用该值）")
    private String reCouponFlag;

    @ApiModelProperty(value = "适用商品类型:0-自动义商品范围 1-全部")
    private String applyProductFlag;

    @Override
    public String getId() {
        return uuid;
    }

    @Override
    public void setId(String id) {
        this.uuid = id;
    }
}

