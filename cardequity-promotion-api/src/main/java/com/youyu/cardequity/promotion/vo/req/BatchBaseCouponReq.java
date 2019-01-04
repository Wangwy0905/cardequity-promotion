package com.youyu.cardequity.promotion.vo.req;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

/**
 * Created by caiyi on 2019/1/2.
 */
@Data
public class BatchBaseCouponReq {
    @ApiModelProperty(value = "指定优惠券列表")
    private List<BaseCouponReq> baseCouponList;
}
