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
 * @date 2019年5月20日 10:00:00
 * @work 优惠券状态上下架req
 */
@ApiModel("优惠券状态上下架req")
@Setter
@Getter
public class ProductCouponStatusReq implements Serializable {

    private static final long serialVersionUID = -1341600815846649506L;

    @ApiModelProperty("优惠券id列表")
    private List<String> productCouponIds = new ArrayList<>();

    @ApiModelProperty(value = "状态 0:下架 1:上架")
    private String status;
}
