package com.youyu.cardequity.promotion.vo.req;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

/**
 * 商品基本信息
 */
@Data
public class BaseProductReq {
    @ApiModelProperty(value = "商品编号:必填", required = true)
    private String productId;

    @ApiModelProperty(value = "子商品编号:必填", required = true)
    private String skuId;
}
