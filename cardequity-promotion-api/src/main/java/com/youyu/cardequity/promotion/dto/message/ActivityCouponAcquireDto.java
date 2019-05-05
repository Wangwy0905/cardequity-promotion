package com.youyu.cardequity.promotion.dto.message;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

/**
 * @author panqingqing
 * @version v1.0
 * @date 2019年04月25日 15:00:00
 * @work 活动发放用户优惠券消息dto
 */
@Setter
@Getter
@ApiModel("活动发放用户优惠券消息dto")
public class ActivityCouponAcquireDto implements Serializable {

    private static final long serialVersionUID = 4258780906798927696L;

    @ApiModelProperty("活动id")
    private String activityId;

    @ApiModelProperty("客户id")
    private String clientId;

    @ApiModelProperty("优惠券id")
    private String couponId;
}
