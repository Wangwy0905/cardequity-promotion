package com.youyu.cardequity.promotion.dto.req;

import com.youyu.cardequity.common.base.base.PageQuery;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

/**
 * @author panqingqing
 * @version v1.0
 * @date 2019年5月20日 10:00:00
 * @work 优惠券查询req
 */
@ApiModel("优惠券查询req")
@Setter
@Getter
public class ProductCouponQueryReq extends PageQuery {

    private static final long serialVersionUID = -5037938426207342405L;

    @ApiModelProperty(value = "领取对象 *:全部用户 10:注册 11:会员")
    private String clientTypeSet;

    @ApiModelProperty(value = "优惠券搜索条件")
    private String searchCondition;

    @ApiModelProperty(value = "类型 1:消费券 2:运费券")
    private String couponType;

    @ApiModelProperty(value = "级别 0:小鱼券 1:大鱼券")
    private String couponLevel;

    @ApiModelProperty(value = "状态 0:下架 1:上架")
    private String status;

    @ApiModelProperty(value = "领取状态 0:待领取 1:领取中 2:已结束 3:-")
    private String getStatus;

    @ApiModelProperty(value = "券状态 0:未开始 1:有效中 2:已过期 3:-")
    private String couponStatus;

    @ApiModelProperty(value = "领取方式 0:后台发放 1:用户领取")
    private String getType;
}
