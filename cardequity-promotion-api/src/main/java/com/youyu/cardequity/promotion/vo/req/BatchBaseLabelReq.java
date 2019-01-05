package com.youyu.cardequity.promotion.vo.req;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

/**
 * Created by caiyi on 2019/1/5.
 */
@Data
public class BatchBaseLabelReq {

    @ApiModelProperty(value = "指定标签列表")
    private List<BaseLabelReq> labelList;
}
