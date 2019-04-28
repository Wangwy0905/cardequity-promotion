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
 * @work 优惠券发放查询Rsp
 */
@Setter
@Getter
@ApiModel("优惠券发放查询Rsp")
public class CouponIssueQueryRsp implements Serializable {

    private static final long serialVersionUID = 1500046571279506434L;

    @ApiModelProperty("发放id")
    private String couponIssueId;

    @ApiModelProperty("优惠券id")
    private String couponId;

    @ApiModelProperty("优惠券名称")
    private String couponName;

    @ApiModelProperty("对象类型 1:用户id 2:活动id")
    private String targetType;

    @ApiModelProperty("上下架 0:上架 1:下架")
    private String isVisible;

    @ApiModelProperty("发放时间")
    private String issueTime;

    @ApiModelProperty("发放状态 1:未发放 2:发放中 3:已完成")
    private String issueStatus;

    @ApiModelProperty("操作者")
    private String operator;

    @ApiModelProperty("编辑标志 true:不显示 false:显示")
    private Boolean editFlag;

    @ApiModelProperty("删除标志 true:不显示 false:显示")
    private Boolean deleteFlag;
}
