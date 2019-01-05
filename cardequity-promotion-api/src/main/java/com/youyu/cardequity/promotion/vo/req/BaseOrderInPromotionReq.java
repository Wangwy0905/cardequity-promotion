package com.youyu.cardequity.promotion.vo.req;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * Created by caiyi on 2019/1/5.
 */
@Data
public class BaseOrderInPromotionReq {
    @ApiModelProperty(value = "客户信息")
    private String clientId;

    @ApiModelProperty(value = "订单号")
    private String orderId;
}
