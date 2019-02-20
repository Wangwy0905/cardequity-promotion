package com.youyu.cardequity.promotion.vo.rsp;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * Created by caiyi on 2019/2/20.
 */
@Data
public class FindClientCouponNumReq {
    @ApiModelProperty(value = "优惠券数量")
    private int couponNum;

    @ApiModelProperty(value = "新领优惠券数")
    private int newNum;
}
