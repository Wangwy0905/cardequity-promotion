package com.youyu.cardequity.promotion.vo.req;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

@Data
public class CouponRefAllProductReq {
    @ApiModelProperty(value = "优惠券id")
    private String couponId;
    @ApiModelProperty(value = "商品集合")
    private List<BaseProductReq>  productList;
}
