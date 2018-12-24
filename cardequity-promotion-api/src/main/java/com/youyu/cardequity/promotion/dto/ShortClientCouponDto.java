package com.youyu.cardequity.promotion.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

/**
 * Created by caiyi on 2018/12/21.
 */
@Getter
@Setter
public class ShortClientCouponDto extends ShortCouponDetailDto {
    @ApiModelProperty(value = "领取编号:")
    private String uuid;
}
