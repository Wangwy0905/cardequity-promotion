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

    @ApiModelProperty(value = "操作者：用于更新产生者或更新者，一般传网关获取的ip")
    private String operator;
}
