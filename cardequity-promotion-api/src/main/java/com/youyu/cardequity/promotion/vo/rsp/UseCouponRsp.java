package com.youyu.cardequity.promotion.vo.rsp;

import com.youyu.cardequity.promotion.dto.ClientCouponDto;
import com.youyu.cardequity.promotion.dto.OrderProductDetailDto;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by caiyi on 2018/12/14.
 */
@Getter
@Setter
public class UseCouponRsp {
    public UseCouponRsp(){
        productLsit=new ArrayList<>();
    }

    @ApiModelProperty(value = "领取的优惠券")
    private ClientCouponDto clientCoupon;

    @ApiModelProperty(value = "实际优惠金额")
    private BigDecimal profitAmount;

    @ApiModelProperty(value = "叠加标识")
    private String reCouponFlag;

    @ApiModelProperty(value = "相关商品明细，关联该券使用的商品及数量")
    private List<OrderProductDetailDto> productLsit;
}
