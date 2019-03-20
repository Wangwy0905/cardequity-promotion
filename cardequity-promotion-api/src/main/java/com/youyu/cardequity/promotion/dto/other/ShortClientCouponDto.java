package com.youyu.cardequity.promotion.dto.other;

import com.youyu.cardequity.promotion.dto.other.ShortCouponDetailDto;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 客户优惠券基本信息
 */
@Data
public class ShortClientCouponDto extends ShortCouponDetailDto {
    @ApiModelProperty(value = "领取编号:")
    private String uuid;
}
