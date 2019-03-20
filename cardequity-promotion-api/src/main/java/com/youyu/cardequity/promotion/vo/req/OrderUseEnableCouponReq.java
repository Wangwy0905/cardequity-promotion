package com.youyu.cardequity.promotion.vo.req;

import com.youyu.cardequity.promotion.dto.other.OrderProductDetailDto;
import com.youyu.cardequity.promotion.dto.other.ShortClientCouponDto;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

/**
 * 订单可用优惠请求体
 */
@Data
public class OrderUseEnableCouponReq extends OperatReq{

    @ApiModelProperty(value = "活动编号:关联的活动编号")
    private String activityId;

    @ApiModelProperty(value = "订单编号:关联的订单编号")
    private String orderId;

    @ApiModelProperty(value = "本次订单未优惠前运费")
    private BigDecimal transferFare;

    @ApiModelProperty(value = "操作者：用于更新产生者或更新者，一般传网关获取的ip")
    private String operator;

    @ApiModelProperty(value = "相关商品明细，用于判断是否满足使用阶梯门槛")
    private List<OrderProductDetailDto> productList;
}
