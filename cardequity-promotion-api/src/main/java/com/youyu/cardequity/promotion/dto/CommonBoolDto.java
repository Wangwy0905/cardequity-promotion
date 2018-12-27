package com.youyu.cardequity.promotion.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

/**
 * Created by caiyi on 2018/12/14.
 */
@Getter
@Setter
public class CommonBoolDto<T> {
    public CommonBoolDto(){}

    public CommonBoolDto(boolean success){this.setSuccess(success);}
    @ApiModelProperty(value = "是否成功:true-成功")
    private boolean success;

    @ApiModelProperty(value = "描述信息")
    private String desc;

    @ApiModelProperty(value = "执行码")
    private String Code;

    @ApiModelProperty(value = "数据")
    private T data;

    public boolean getSuccess() {
        return success;
    }
}
