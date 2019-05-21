package com.youyu.cardequity.promotion.dto.req;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotNull;
import java.io.Serializable;

/**
 * @author panqingqing
 * @version v1.0
 * @date 2019年5月20日 10:00:00
 * @work 优惠券查看req
 */
@ApiModel("优惠券查看req")
@Setter
@Getter
public class ProductCouponViewReq implements Serializable {

    private static final long serialVersionUID = 8179394915922133591L;

    @NotNull
    @ApiModelProperty("优惠券Id")
    private String productCouponId;
}
