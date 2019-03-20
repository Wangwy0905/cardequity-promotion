package com.youyu.cardequity.promotion.vo.req;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

/**
 *  2018/12/27 徐长焕 增加基本信息传递
 */
@Data
public class BaseClientReq {
    @ApiModelProperty(value = "客户编号:必填", required = true)
    private String clientId;
}
