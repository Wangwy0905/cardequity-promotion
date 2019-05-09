package com.youyu.cardequity.promotion.dto.req;

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
 * @work 优惠券发放编辑请求Req
 */
@Setter
@Getter
@ApiModel("优惠券发放编辑请求Req")
public class CouponIssueEditReq implements Serializable {

    private static final long serialVersionUID = 4042946867194036437L;

    @ApiModelProperty("优惠券发放id")
    private String couponIssueId;

    @ApiModelProperty("优惠券id")
    private String couponId;

    @ApiModelProperty("目标对象类型 1:用户id 2:活动id")
    private String targetType;

    @ApiModelProperty("优惠券发放时间")
    private String issueTime;

    @ApiModelProperty("上下架 0:上架 1:下架")
    private String isVisible;

    @ApiModelProperty("优惠券发放对象id")
    private List<String> issueIds = new ArrayList<>();
}
