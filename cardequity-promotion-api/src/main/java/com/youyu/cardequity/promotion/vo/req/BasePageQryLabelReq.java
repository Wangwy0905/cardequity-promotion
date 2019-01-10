package com.youyu.cardequity.promotion.vo.req;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * Created by caiyi on 2019/1/10.
 */
@Data
public class BasePageQryLabelReq extends BaseQryLabelReq {
    @ApiModelProperty(value = "页码：从1开始")
    private int pageNo;

    @ApiModelProperty(value = "每页数量：从1开始")
    private int pageSize;
}
