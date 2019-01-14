package com.youyu.cardequity.promotion.vo.req;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * Created by caiyi on 2019/1/14.
 */
@Data
public class BaseClientProductReq{
    @ApiModelProperty(value = "客户编号:必填", required = true)
    private String clientId;

    @ApiModelProperty(value = "商品编号:")
    private String productId;

    @ApiModelProperty(value = "子商品编号:")
    private String skuId;
}
