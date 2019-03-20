package com.youyu.cardequity.promotion.dto.other;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 执行结果描述
 */
@Data
public class CommonBoolDto<T> {
    public CommonBoolDto(){}

    public CommonBoolDto(boolean success){this.setSuccess(success);}
    @ApiModelProperty(value = "是否成功:true-成功")
    private boolean success;

    @ApiModelProperty(value = "描述信息")
    private String desc;

    @ApiModelProperty(value = "执行码")
    private String code;

    @ApiModelProperty(value = "数据")
    private T data;

    public boolean getSuccess() {
        return success;
    }
}
