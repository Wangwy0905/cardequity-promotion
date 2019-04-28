package com.youyu.cardequity.promotion.dto.req;


import com.youyu.cardequity.common.base.base.PageQuery;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

/**
 * @author panqingqing
 * @version v1.0
 * @date 2019年04月25日 15:00:00
 * @work 优惠券发放查询Req
 */
@Setter
@Getter
@ApiModel("优惠券发放查询Req")
public class CouponIssueQueryReq extends PageQuery {

    private static final long serialVersionUID = -470172795098815569L;

    @ApiModelProperty("搜索条件:优惠券名称或id")
    private String searchCondition;

    @ApiModelProperty("对象类型 1:用户id 2:活动id")
    private String targetType;

    @ApiModelProperty("上下架 0:上架 1:下架")
    private String isVisible;

    @ApiModelProperty("发放状态 1:未发放 2:发放中 3:已完成")
    private String issueStatus;
}
