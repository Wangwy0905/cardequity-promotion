package com.youyu.cardequity.promotion.vo.rsp;

import com.youyu.cardequity.promotion.dto.other.CouponDetailDto;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by caiyi on 2019/1/16.
 */
@Data
public class FindCouponListByOrderDetailRsp {
    public FindCouponListByOrderDetailRsp(){
        couponEnableList=new ArrayList<>();
        couponUnEnableList=new ArrayList<>();
    }
    @ApiModelProperty(value = "可用优惠券")
    private List<FullClientCouponRsp>  couponEnableList;

    @ApiModelProperty(value = "不可用优惠券：不含过期的")
    private List<FullClientCouponRsp>  couponUnEnableList;
}
