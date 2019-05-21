package com.youyu.cardequity.promotion.dto.rsp;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

/**
 * @author panqingqing
 * @version v1.0
 * @date 2019年5月20日 10:00:00
 * @work 优惠券关联商品查询rsp
 */
@ApiModel("优惠券关联商品查询rsp")
@Setter
@Getter
public class CouponRefProductQueryRsp implements Serializable {

    private static final long serialVersionUID = -1189518105019196660L;

    @ApiModelProperty("商品id")
    private String productId;

    @ApiModelProperty("商品名称")
    private String productName;

    @ApiModelProperty("供应商名称")
    private String supplierName;

    @ApiModelProperty("三级分类")
    private String thirdCategoryName;

    @ApiModelProperty("关联优惠券数量")
    private Integer refCouponQuantity;
}
