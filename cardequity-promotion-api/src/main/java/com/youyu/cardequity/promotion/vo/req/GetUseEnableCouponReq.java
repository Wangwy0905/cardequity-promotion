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

    @ApiModelProperty(value = "指定使用的优惠券：每个级别（大鱼券券和小鱼券）只能指定一个+运费券")
    private List<ShortClientCouponDto> obtainCouponList;

    @ApiModelProperty(value = "指定使用活动信息：订单确定时传入只做交易")
    private List<BaseActivityReq> activityList;
}
