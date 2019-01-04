package com.youyu.cardequity.promotion.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

/**
 * 订单涉及商品信息：订单下对应商品、价格、数量
 */
@Data
@Getter
@Setter
public class OrderProductDetailDto {
    public OrderProductDetailDto(){
        totalAmount=BigDecimal.ZERO;
        profitAmount=BigDecimal.ZERO;
        profitCount=BigDecimal.ZERO;
        price=BigDecimal.ZERO;
        appCount=BigDecimal.ZERO;
    }
    @ApiModelProperty(value = "商品id")
    private String productId;

    @ApiModelProperty(value = "库存量id")
    private String skuId;

    @ApiModelProperty(value = "适用总额：参与优惠的总金额=单价*数量")
    private BigDecimal totalAmount;

    @ApiModelProperty(value = "价格")
    private BigDecimal price;

    @ApiModelProperty(value = "数量")
    private BigDecimal appCount;

    @ApiModelProperty(value = "优惠金额：优惠使用后计算得到")
    private BigDecimal profitAmount;

    @ApiModelProperty(value = "适用优惠的商品数量")
    private BigDecimal profitCount;
}
