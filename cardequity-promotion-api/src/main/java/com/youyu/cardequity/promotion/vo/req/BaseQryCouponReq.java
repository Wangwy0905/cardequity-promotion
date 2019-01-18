package com.youyu.cardequity.promotion.vo.req;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * Created by caiyi on 2019/1/2.
 */
@Data
public class BaseQryCouponReq {
    @ApiModelProperty(value = "指定优惠券id")
    private String couponId;

    @ApiModelProperty(value = "券状态:0-未开始 1-有效中 2-已过期 3-未过期")
    private String couponStatus;

    @ApiModelProperty(value = "优惠券名称：可模糊")
    private String couponName;

    @ApiModelProperty(value = "商品id")
    private String productId;

    @ApiModelProperty(value = "子商品id")
    private String skuId;

    @ApiModelProperty(value = "领取对象：0-全部用户 1-新用户 2-会员")
    private String targetFlag;

    @ApiModelProperty(value = "券类型：0-1级 1-2级(全局券) 2-运费券")
    private String couponType;

    @ApiModelProperty(value = "发放状态：0-待发放 1-发放中 2-已停止")
    private String sendStatus;

    @ApiModelProperty(value = "页码：从1开始")
    private int pageNo;

    @ApiModelProperty(value = "每页数量：从1开始")
    private int pageSize;
}
