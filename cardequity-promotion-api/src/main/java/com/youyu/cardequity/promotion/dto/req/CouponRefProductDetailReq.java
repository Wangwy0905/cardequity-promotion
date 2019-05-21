package com.youyu.cardequity.promotion.dto.req;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

/**
 * @author panqingqing
 * @version v1.0
 * @date 2019年5月20日 10:00:00
 * @work 优惠券关联商品详情req
 */
@ApiModel("优惠券关联商品详情req")
@Setter
@Getter
public class CouponRefProductDetailReq implements Serializable {

    private static final long serialVersionUID = 6312560143215100526L;

    @ApiModelProperty("商品id")
    private String productId;

    @ApiModelProperty("商品名称")
    private String productName;

    @ApiModelProperty("供应商名称")
    private String supplierName;

    @ApiModelProperty("三级分类名称")
    private String thirdCategoryName;
}
