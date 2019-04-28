package com.youyu.cardequity.promotion.dto.req;

import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.List;


//todo desc

/**
 * @Auther: zjm
 * @Date: 2019-04-25
 * @Description: 优惠券发放信息细节，
 */
@Setter
@Getter
public class CouponIssueMsgDetailsReq implements Serializable {
    private static final long serialVersionUID = 180287249299698899L;

    @ApiModelProperty("券发放任务ID")
    private String couponIssueId;

    @ApiModelProperty("预发放目标用户的相关信息，包括：用户类型，用户ID等标识")
    private List<UserInfo4CouponIssueDto> userInfo4CouponIssueDtoList;

    @ApiModelProperty("准备发放的券ID")
    private String couponId;

    @ApiModelProperty("发放的目标对象：1.用户ID；2.活动ID")
    private String targetType;
}
