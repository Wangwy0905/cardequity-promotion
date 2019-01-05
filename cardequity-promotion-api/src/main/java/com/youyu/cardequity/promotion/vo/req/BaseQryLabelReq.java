package com.youyu.cardequity.promotion.vo.req;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * Created by caiyi on 2019/1/5.
 */
@Data
public class BaseQryLabelReq {
    @ApiModelProperty(value = "标签名称:")
    private String labelName;

    @ApiModelProperty(value = "标签使用类型:0-优惠券 1-活动")
    private String labelType;
}
