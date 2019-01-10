package com.youyu.cardequity.promotion.vo.rsp;


import com.youyu.cardequity.promotion.enums.CommonDict;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.Setter;

/**
 * Created by caiyi on 2019/1/10.
 */
@Data
public class GatherInfoRsp {
    @ApiModelProperty(value = "统计项")
    private String gatherItem;

    @ApiModelProperty(value = "统计项描述")
    private String gatherName;

    @ApiModelProperty(value = "统计值")
    private int gatherValue;
}
