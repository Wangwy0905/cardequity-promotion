package com.youyu.cardequity.promotion.vo.rsp;

import com.youyu.cardequity.promotion.dto.ClientCouponDto;
import com.youyu.cardequity.promotion.dto.other.CouponDetailDto;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 客户领取优惠券详情
 */
@Data
public class FullClientCouponRsp {
        @ApiModelProperty(value = "优惠详情")
        private CouponDetailDto coupon;

        @ApiModelProperty(value = "优惠详情")
        private ClientCouponDto clientCoupon;
}
