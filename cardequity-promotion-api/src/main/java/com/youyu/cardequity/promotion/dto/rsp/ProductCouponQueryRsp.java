package com.youyu.cardequity.promotion.dto.rsp;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * @author panqingqing
 * @version v1.0
 * @date 2019年5月20日 10:00:00
 * @work 优惠券查询rsp
 */
@ApiModel("优惠券查询rsp")
@Setter
@Getter
public class ProductCouponQueryRsp implements Serializable {

    private static final long serialVersionUID = -8463159423821874573L;

    @ApiModelProperty(value = "优惠券id")
    private String productCouponId;

    @ApiModelProperty(value = "优惠券名称")
    private String couponName;

    @ApiModelProperty("优惠券类型值")
    private String couponTypeValue;

    @ApiModelProperty("优惠券金额")
    private BigDecimal couponAmount;

    @ApiModelProperty("优惠券门槛")
    private String couponCondition;

    @ApiModelProperty("优惠券封顶金额")
    private String profitTopAmount;

    @ApiModelProperty("领取方式")
    private String getTypeValue;

    @ApiModelProperty("领取状态")
    private String getStatusValue;

    @ApiModelProperty("优惠券状态")
    private String couponStatusValue;

    @ApiModelProperty(value = "状态 0:下架 1:上架")
    private String status;
}
