package com.youyu.cardequity.promotion.dto.req;

import com.youyu.cardequity.common.base.base.PageQuery;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

/**
 * @Auther: zjm
 * @Date: 2019-04-29
 * @Description: clientCouponStatus的详细信息见  {@link ClientCouponStatusConstant}
 */
@Setter
@Getter
@ApiModel("优惠券发放流水查询Req")
public class CouponIssueHistoryQueryReq extends PageQuery {
    private static final long serialVersionUID = 8592469364904251216L;

    @ApiModelProperty("搜索条件：用户ID")
    private String searchCondition;

    @ApiModelProperty("券状态：0-未使用；2-已使用；3-已过期;4-发放失败或未发放")
    private String clientCouponStatus;

    @ApiModelProperty("优惠券发放记录ID")
    private String couponIssueId;
}
