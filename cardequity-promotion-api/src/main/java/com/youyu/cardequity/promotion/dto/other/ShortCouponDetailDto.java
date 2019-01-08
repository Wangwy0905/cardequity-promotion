package com.youyu.cardequity.promotion.dto.other;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * Created by caiyi on 2018/12/10.
 * 优惠券与阶段的简短传输实体
 */
@Data
@ApiModel
public class ShortCouponDetailDto {

    @ApiModelProperty(value = "阶段编号:")
    private String stageId;

    @ApiModelProperty(value = "优惠券编号:")
    private String couponId;
}
