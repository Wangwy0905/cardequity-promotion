package com.youyu.cardequity.promotion.vo.req;

import com.youyu.cardequity.promotion.dto.ProductCouponDto;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

/**
 * Created by caiyi on 2018/12/27.
 */
@Getter
@Setter
public class CouponDetailReq {
    @ApiModelProperty(value = "优惠券基本信息" ,required= true)
    private ProductCouponDto productCouponDto;
}
