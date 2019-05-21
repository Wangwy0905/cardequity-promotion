package com.youyu.cardequity.promotion.dto.req;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * @author panqingqing
 * @version v1.0
 * @date 2019年5月20日 10:00:00
 * @work 优惠券关联商品req
 */
@ApiModel("优惠券关联商品req")
@Setter
@Getter
public class CouponRefProductReq implements Serializable {

    private static final long serialVersionUID = 3998279151830014373L;

    @ApiModelProperty("商品优惠券id")
    private String productCouponId;

    @ApiModelProperty("商品详情")
    private List<CouponRefProductDetailReq> couponRefProductDetailReqs = new ArrayList<>();
}
