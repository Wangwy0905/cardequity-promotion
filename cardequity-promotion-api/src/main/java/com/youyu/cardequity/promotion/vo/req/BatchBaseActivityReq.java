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
}
