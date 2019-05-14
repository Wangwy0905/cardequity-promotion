package com.youyu.cardequity.promotion.dto.req;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

/**
 * @author panqingqing
 * @version v1.0
 * @date 2019年5月14日 10:00:00
 * @work 会员商品最大优惠券请求req
 */
@Setter
@Getter
@ApiModel("会员商品最大优惠券请求req")
public class MemberProductMaxCouponReq implements Serializable {

    private static final long serialVersionUID = 6808415551160889084L;

    @ApiModelProperty("商品Id")
    private String productId;

}
