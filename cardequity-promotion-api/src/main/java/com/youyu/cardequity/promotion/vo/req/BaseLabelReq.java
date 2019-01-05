package com.youyu.cardequity.promotion.vo.req;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * Created by caiyi on 2019/1/5.
 */
@Data
public class BaseLabelReq {
    @ApiModelProperty(value = "编号:")
    private String uuid;
}
