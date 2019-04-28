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
 * @work 优惠券发放上下架请求Req
 */
@Setter
@Getter
@ApiModel("优惠券发放上下架请求Req")
public class CouponIssueVisibleReq implements Serializable {

    private static final long serialVersionUID = -6584456797812712409L;

    @ApiModelProperty("发放id列表")
    private List<String> couponIssueIds = new ArrayList<>();

    @ApiModelProperty("上下架 0:上架 1:下架")
    private String isVisible;
}
