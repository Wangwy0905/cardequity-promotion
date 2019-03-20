package com.youyu.cardequity.promotion.vo.req;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * Created by caiyi on 2019/1/4.
 */
@Data
public class BaseActivityReq {
    @ApiModelProperty(value = "指定优惠id")
    private String activityId;

    @ApiModelProperty(value = "指定子优惠id")
    private String stageId;
}
