package com.youyu.cardequity.promotion.dto.rsp;

import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

/**
 * @Auther: zjm
 * @Date: 2019-04-29
 * @Description:
 */
@Getter
@Setter
public class CouponIssueHistoryQueryRep implements Serializable {

    private static final long serialVersionUID = -3841653988842080308L;

    @ApiModelProperty("序号")
    private String sequenceNumber;

    @ApiModelProperty("用户账号ID")
    private String clientId;

    //todo 保留了使用中
    @ApiModelProperty("券状态：0-未使用；2-已使用；3-已过期;4-发放失败或未发放")
    private String clientCouponStatus;

    @ApiModelProperty("使用时间：券状态为已使用才会有值")
    private String usedDate;

    @ApiModelProperty("订单编号")
    private String orderId;

    @ApiModelProperty("用户手机号码")
    private String mobile;
}
