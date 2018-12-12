package com.youyu.cardequity.promotion.vo.rsp;

import com.youyu.cardequity.promotion.dto.CouponStageUseAndGetRuleDto;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.List;

/**
 * Created by caiyi on 2018/12/10.
 */
@Getter
@Setter
public class CouponDefineRsp {
    @ApiModelProperty(value = "优惠券编号:")
    private String uuid;

    @ApiModelProperty(value = "优惠策略类型:0-折扣券(该表不会出现) 1-阶梯优惠券(满多少减多少)  2-定额优惠券（该券无阶梯优惠固定金额）")
    private String couponStrategyType;

    @ApiModelProperty(value = "类型:0-红包 1-优惠券 2-运费券")
    private String couponType;

    @ApiModelProperty(value = "优惠名称:")
    private String couponName;

    @ApiModelProperty(value = "优惠标签:标签：满返券、促销等")
    private String couponLable;

    @ApiModelProperty(value = "优惠短描:如满3件减20")
    private String couponShortDesc;

    @ApiModelProperty(value = "专属客户类型集合:订单涉及门槛属性：只有这些这些客户类型才提供优惠。多种客户类型用逗号相隔，为*标识所有")
    private String clientTypeSet;

    @ApiModelProperty(value = "专属银行卡集合:订单涉及门槛属性：只有这些这些银行卡买的才提供优惠。")
    private String bankCodeSet;

    @ApiModelProperty(value = "专属支付方式集合:订单涉及门槛属性：只有指定的支付方式去买才提供优惠。如可以配置值允许信用卡参与，为*标识所有")
    private String payTypeSet;

    @ApiModelProperty(value = "优惠值:如果是阶梯或随机的填0，存折扣、金额")
    private BigDecimal profitValue;

    @ApiModelProperty(value = "关联活动Id:多个券可用关联一个活动(组成活动大礼包)，但是一个券不能关联多个活动，只能再定义一张券：一般不设置获取规则，获取规则直接由活动进行自动发放")
    private String joinActivityId;

    @ApiModelProperty(value = "叠加标志:0-不可叠加 1-可叠加 2-自定义（建议规则简单点，不采用该值）")
    private String reCouponFlag;

    @ApiModelProperty(value = "优惠的阶梯")
    private List<CouponStageUseAndGetRuleDto> CouponStageDtoList;


}
