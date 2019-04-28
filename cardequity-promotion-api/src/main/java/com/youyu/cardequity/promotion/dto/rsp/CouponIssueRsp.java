package com.youyu.cardequity.promotion.dto.rsp;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

/**
 * @author panqingqing
 * @version v1.0
 * @date 2019年04月25日 15:00:00
 * @work 优惠券发放响应Rsp
 */
@Setter
@Getter
@ApiModel("优惠券发放响应Rsp")
public class CouponIssueRsp implements Serializable {

    private static final long serialVersionUID = 2740882892871271699L;

    @ApiModelProperty("优惠券发放id")
    private String couponIssueId;

}
