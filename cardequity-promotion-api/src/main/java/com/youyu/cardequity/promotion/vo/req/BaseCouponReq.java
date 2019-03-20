package com.youyu.cardequity.promotion.vo.req;

import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

/**
 * Created by caiyi on 2018/12/27.
 */
@Getter
@Setter
public class BaseCouponReq {
    @ApiModelProperty(value = "指定优惠券id")
    private String couponId;

    @ApiModelProperty(value = "指定子优惠券id")
    private String stageId;

}
