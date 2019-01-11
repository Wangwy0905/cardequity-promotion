package com.youyu.cardequity.promotion.vo.rsp;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;

/**
 * Created by caiyi on 2019/1/11.
 */
@Data
public class BasePriceActivityRsp {
    @ApiModelProperty(value = "商品编号:必填", required = true)
    private String ProductId;

    @ApiModelProperty(value = "子商品编号:必填")
    private String skuId;

    @ApiModelProperty(value = "活动价格")
    private BigDecimal price;
}
