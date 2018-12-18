package com.youyu.cardequity.promotion.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

/**
 * 订单涉及商品信息：订单下对应商品、价格、数量
 */
@Getter
@Setter
public class OrderProductDetailDto {
    @ApiModelProperty(value = "商品id")
    private String productId;

    @ApiModelProperty(value = "价格")
    private BigDecimal price;

    @ApiModelProperty(value = "数量")
    private BigDecimal appCount;

    @ApiModelProperty(value = "商品组id")
    private String groupId;

    @ApiModelProperty(value = "优惠金额")
    private BigDecimal ProfitAmount;
}
