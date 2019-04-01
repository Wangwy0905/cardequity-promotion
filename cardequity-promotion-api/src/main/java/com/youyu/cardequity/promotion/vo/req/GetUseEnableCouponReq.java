package com.youyu.cardequity.promotion.vo.req;

import com.youyu.cardequity.promotion.dto.other.OrderProductDetailDto;
import com.youyu.cardequity.promotion.dto.other.ShortClientCouponDto;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.List;

/**
 * 订单使用或获取可用优惠券请求体
 */
@Data
public class GetUseEnableCouponReq extends OrderUseEnableCouponReq{

    @ApiModelProperty(value = "指定使用的优惠券：1.如果需要后台自动计算传null；" +
                              "2.如果客户想什么优惠券都不使用传空数组；" +
                              "3.系统自动智能计算的优惠券数如果客户没有变动需要指定传入；" +
                              "4.客户指定传入的优惠券数组再次按规则：每个级别（大鱼券和小鱼券）只能指定一个+运费券")
    private List<ShortClientCouponDto> obtainCouponList;

    @ApiModelProperty(value = "指定使用活动信息：订单确定时传入只做交易")
    private List<BaseActivityReq> activityList;
}
