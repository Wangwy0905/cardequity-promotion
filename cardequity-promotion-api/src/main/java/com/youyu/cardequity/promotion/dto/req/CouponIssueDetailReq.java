package com.youyu.cardequity.promotion.dto.req;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

/**
 * @author panqingqing
 * @version v1.0
 * @date 2019年04月25日 15:00:00
 * @work 优惠券发放明细请求Req
 */
@Setter
@Getter
@ApiModel("优惠券发放明细请求Req")
public class CouponIssueDetailReq implements Serializable {

    private static final long serialVersionUID = -2593501196694186663L;

    @ApiModelProperty("发放id")
    private String couponIssueId;
}
