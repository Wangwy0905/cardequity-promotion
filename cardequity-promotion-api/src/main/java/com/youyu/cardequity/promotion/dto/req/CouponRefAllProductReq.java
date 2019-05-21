package com.youyu.cardequity.promotion.dto.req;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

/**
 * @author panqingqing
 * @version v1.0
 * @date 2019年5月20日 10:00:00
 * @work 优惠券关联所有商品req
 */
@ApiModel("优惠券关联所有商品req")
@Setter
@Getter
public class CouponRefAllProductReq implements Serializable {

    private static final long serialVersionUID = -486037893422902907L;

    @ApiModelProperty("商品优惠券id")
    private String productCouponId;

    @ApiModelProperty("商品总数")
    private Integer productSum;
}
