package com.youyu.cardequity.promotion.vo.req;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * Created by caiyi on 2019/1/12.
 */
@Data
public class FindProductInValidActivityReq {
    @ApiModelProperty(value = "促销状态:0-未开始 1-活动中 2-已结束")
    private String status;

    @ApiModelProperty(value = "可多选，直接拼接串如“013”，选项如下：0-限额任选 1-折扣 2-优惠价 3-现金立减 4-自动返券(暂无)")
    private String activityCouponType;

}
