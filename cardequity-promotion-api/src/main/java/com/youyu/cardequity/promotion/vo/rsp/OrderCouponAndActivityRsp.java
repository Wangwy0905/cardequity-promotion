package com.youyu.cardequity.promotion.vo.rsp;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by caiyi on 2019/1/4.
 */
@Data
public class OrderCouponAndActivityRsp {

    public OrderCouponAndActivityRsp(){
        commonCoupons=new ArrayList<>();
        transferCoupons=new ArrayList<>();
        activities=new ArrayList<>();
    }

    @ApiModelProperty(value = "普通优惠券使用情况：一般只允许使用两张,大鱼券，小鱼券")
    private List<UseCouponRsp> commonCoupons;

    @ApiModelProperty(value = "运费券使用情况：一般只允许使用一张")
    private List<UseCouponRsp> transferCoupons;

    @ApiModelProperty(value = "活动使用情况：一般有多条")
    private List<UseActivityRsp> activities;
}
