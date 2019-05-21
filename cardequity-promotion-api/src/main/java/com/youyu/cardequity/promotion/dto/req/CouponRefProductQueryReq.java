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
 * @work 优惠券关联商品查询req
 */
@ApiModel("优惠券关联商品查询req")
@Setter
@Getter
public class CouponRefProductQueryReq extends PageQuery {

    private static final long serialVersionUID = -1189518105019196660L;

    @ApiModelProperty("商品优惠券id")
    private String productCouponId;

    @ApiModelProperty("搜索条件")
    private String searchCondition;

    @ApiModelProperty("三级分类")
    private String thirdCategoryName;

    @ApiModelProperty("供应商名称")
    private String supplierName;
}
