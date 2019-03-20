package com.youyu.cardequity.promotion.vo.rsp;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * Created by caiyi on 2019/1/10.
 */
@Data
public class GatherCouponByProductRsp {
    @ApiModelProperty(value = "商品编号")
    private String productId;

    @ApiModelProperty(value = "子编号")
    private String skuId;

    @ApiModelProperty(value = "相关优惠券或活动数量")
    private int gatherValue;
}
