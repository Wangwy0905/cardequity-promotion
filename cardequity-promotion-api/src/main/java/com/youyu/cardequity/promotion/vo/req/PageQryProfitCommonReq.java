package com.youyu.cardequity.promotion.vo.req;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * Created by caiyi on 2019/1/24.
 */
@Data
public class PageQryProfitCommonReq extends QryProfitCommonReq {
    @ApiModelProperty(value = "页码：从1开始，传0默认查询全部")
    private int pageNo;

    @ApiModelProperty(value = "每页数量：从1开始，传0默认查询top条数pageNo")
    private int pageSize;
}
