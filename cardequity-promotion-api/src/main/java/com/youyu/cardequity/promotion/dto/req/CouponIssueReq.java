package com.youyu.cardequity.promotion.dto.req;

import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Setter
@Getter
public class CouponIssueReq implements Serializable {

    private static final long serialVersionUID = 3768737223941098951L;

    @ApiModelProperty("优惠券id")
    private String couponId;

    @ApiModelProperty("对象类型 1:用户id 2:活动id")
    private String objectType;

    @ApiModelProperty("优惠券发放时间")
    private String issueTime;

    @ApiModelProperty("优惠券发放对象id")
    private List<String> issueIds = new ArrayList<>();
}
