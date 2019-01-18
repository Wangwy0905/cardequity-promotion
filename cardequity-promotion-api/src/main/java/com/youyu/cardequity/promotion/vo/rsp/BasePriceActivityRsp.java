package com.youyu.cardequity.promotion.vo.rsp;

import com.youyu.cardequity.promotion.dto.other.ActivityViewDto;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Created by caiyi on 2019/1/11.
 */
@Data
public class BasePriceActivityRsp {
    @ApiModelProperty(value = "商品编号:必填", required = true)
    private String productId;

    @ApiModelProperty(value = "子商品编号:必填")
    private String skuId;

    @ApiModelProperty(value = "特价")
    private BigDecimal price;

    @ApiModelProperty(value = "状态:0-下架 1-上架")
    private String status;

    @ApiModelProperty(value = "优惠商品数量")
    private BigDecimal maxCount;

    @ApiModelProperty(value = "活动编号")
    private String id;
}
