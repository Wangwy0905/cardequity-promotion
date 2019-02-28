package com.youyu.cardequity.promotion.vo.req;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * Created by caiyi on 2019/2/28.
 */
@Data
public class FindClientCouponForProductReq {
    @ApiModelProperty(value = "客户编号:必填", required = true)
    private String clientId;

    @ApiModelProperty(value = "商品编号:必填", required = true)
    private String productId;

    @ApiModelProperty(value = "子商品编号:必填", required = true)
    private String skuId;

    @ApiModelProperty(value = "状态 0-正常 1-使用中 2-已使用")
    private String status;

    @ApiModelProperty(value = "期限状态 0-有效期内 1-未开始 2-已过期")
    private String termStatus;
}
