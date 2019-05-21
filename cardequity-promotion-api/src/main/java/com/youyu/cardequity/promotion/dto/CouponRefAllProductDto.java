package com.youyu.cardequity.promotion.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

/**
 * @author panqingqing
 * @version v1.0
 * @date 2019年5月20日 10:00:00
 * @work 优惠券关联所有商品
 */
@ApiModel("优惠券关联所有商品")
@Setter
@Getter
public class CouponRefAllProductDto implements Serializable {

    private static final long serialVersionUID = 4924825128065004239L;

    @ApiModelProperty("商品id")
    private String productId;

    @ApiModelProperty("商品名称")
    private String productName;

    @ApiModelProperty("供应商名称")
    private String supplierName;

    @ApiModelProperty("三级分类名称")
    private String thirdCategoryName;
}

