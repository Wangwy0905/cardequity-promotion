package com.youyu.cardequity.promotion.dto.other;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 按商品分组信息
 */
@Data
public class GroupProductDto {
    @ApiModelProperty(value = "商品id")
    private String productId;

    @ApiModelProperty(value = "最新处理时间")
    private LocalDateTime lastTime;
}
