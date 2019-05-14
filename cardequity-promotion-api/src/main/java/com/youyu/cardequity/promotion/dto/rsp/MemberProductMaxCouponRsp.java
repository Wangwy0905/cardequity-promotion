package com.youyu.cardequity.promotion.dto.rsp;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * @author panqingqing
 * @version v1.0
 * @date 2019年5月14日 10:00:00
 * @work 会员商品最大优惠券响应rsp
 */
@Setter
@Getter
@ApiModel("会员商品最大优惠券响应rsp")
public class MemberProductMaxCouponRsp implements Serializable {

    private static final long serialVersionUID = 9131517730715806458L;

    @ApiModelProperty("优惠金额")
    private BigDecimal profitValue;
}
