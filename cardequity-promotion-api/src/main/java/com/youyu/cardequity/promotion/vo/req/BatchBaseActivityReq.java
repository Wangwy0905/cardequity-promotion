package com.youyu.cardequity.promotion.vo.req;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

/**
 * Created by caiyi on 2019/1/4.
 */
@Data
public class BatchBaseActivityReq {
    @ApiModelProperty(value = "指定优惠券列表")
    private List<BaseActivityReq> baseActivityList;

    @ApiModelProperty(value = "操作者：用于更新产生者或更新者")
    private String operator;
}
