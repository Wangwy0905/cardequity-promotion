package com.youyu.cardequity.promotion.dto;


import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

/**
 * Created by caiyi on 2018/12/12.
 */
@Getter
@Setter
public class ObtainRspDto extends  CommonBoolDto{

    @ApiModelProperty(value = "添加成功后的数据")
    private ClientCouponDto data;

}
