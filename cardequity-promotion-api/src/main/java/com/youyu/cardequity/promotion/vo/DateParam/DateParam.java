package com.youyu.cardequity.promotion.vo.DateParam;

import io.swagger.annotations.ApiModelProperty;
import io.swagger.annotations.ApiOperation;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DateParam {

    @ApiModelProperty(value = "每个月的月末")
    private  String lastMonth;
}
