package com.youyu.cardequity.promotion.dto.rsp;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * @author panqingqing
 * @version v1.0
 * @date 2019年04月25日 15:00:00
 * @work 优惠券发放明细响应Req
 */
@Setter
@Getter
@ApiModel("优惠券发放明细响应Req")
public class CouponIssueDetailRsp implements Serializable {

    private static final long serialVersionUID = 5595991731261020607L;

    @ApiModelProperty("优惠券id")
    private String couponId;

    @ApiModelProperty("优惠券名称")
    private String couponName;

    @ApiModelProperty("优惠券类型:0-红包 1-优惠券 2-运费券")
    private String couponType;

    @ApiModelProperty("优惠券状态:0-上架 1-下架")
    private String couponStatus;

    @ApiModelProperty("发放日期")
    private String issueDate;

    @ApiModelProperty("发放时间")
    private String issueTime;

    @ApiModelProperty("对象类型 1:用户id 2:活动id")
    private String targetType;

    @ApiModelProperty("优惠券发放对象id")
    private List<String> issueIds = new ArrayList<>();

}
