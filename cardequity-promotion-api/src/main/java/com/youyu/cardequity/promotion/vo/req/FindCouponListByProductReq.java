package com.youyu.cardequity.promotion.vo.req;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * Created by caiyi on 2019/2/28.
 */
@Data
public class FindCouponListByProductReq {
    @ApiModelProperty(value = "商品编号:必填", required = true)
    private String productId;

    @ApiModelProperty(value = "子商品编号:必填", required = true)
    private String skuId;

    @ApiModelProperty(value = "包含的券类型0-红包 1-消费券 2-运费券，传多个，无分隔符默认值为01")
    private String containsCouponType;

    @ApiModelProperty(value = "排除标志 0-不排除 1-排除额度、领取频率受限；默认为1")
    private String exclusionFlag;
}
