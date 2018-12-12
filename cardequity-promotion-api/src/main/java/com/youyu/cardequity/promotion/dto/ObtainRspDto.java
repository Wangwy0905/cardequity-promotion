package com.youyu.cardequity.promotion.dto;


import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

/**
 * Created by caiyi on 2018/12/12.
 */
@Getter
@Setter
public class ObtainRspDto {

    @ApiModelProperty(value = "是否成功:true-成功")
    private boolean success;

    @ApiModelProperty(value = "描述信息")
    private String desc;

    @ApiModelProperty(value = "添加成功后的数据")
    private ClientCouponDto data;

    public boolean getSuccess() {
        return success;
    }
}
