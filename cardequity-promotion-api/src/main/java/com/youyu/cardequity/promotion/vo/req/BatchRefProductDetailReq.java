package com.youyu.cardequity.promotion.vo.req;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * Created by caiyi on 2019/2/19.
 */
@Data
public class BatchRefProductDetailReq extends BatchRefProductReq{
    @ApiModelProperty(value = "优惠开始日:到分秒级别")
    private LocalDateTime allowUseBeginDate;

    @ApiModelProperty(value = "优惠结束日:")
    private LocalDateTime allowUseEndDate;
}
