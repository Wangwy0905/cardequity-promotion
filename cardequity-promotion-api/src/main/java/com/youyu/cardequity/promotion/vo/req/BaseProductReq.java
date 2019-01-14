package com.youyu.cardequity.promotion.vo.req;

import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

/**
 * Created by caiyi on 2018/12/27.
 */
@Getter
@Setter
public class BaseProductReq {
    @ApiModelProperty(value = "商品编号:必填", required = true)
    private String productId;

    @ApiModelProperty(value = "子商品编号:必填", required = true)
    private String skuId;
}
