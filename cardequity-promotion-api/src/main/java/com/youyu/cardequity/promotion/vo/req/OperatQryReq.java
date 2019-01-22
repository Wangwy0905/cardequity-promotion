package com.youyu.cardequity.promotion.vo.req;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 查询操作请求体
 */
@Data
public class OperatQryReq extends OperatReq {
    @ApiModelProperty(value = "显示数量：从1开始")
    private int pageSize;
}
