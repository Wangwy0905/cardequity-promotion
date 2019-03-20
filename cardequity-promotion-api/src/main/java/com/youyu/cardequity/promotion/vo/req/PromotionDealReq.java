package com.youyu.cardequity.promotion.vo.req;

import com.youyu.cardequity.promotion.vo.rsp.OrderCouponAndActivityRsp;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * Created by caiyi on 2019/1/5.
 */
@Data
public class PromotionDealReq {
    @ApiModelProperty(value = "订单号")
    private String orderId;

    @ApiModelProperty(value = "订单涉及活动及优惠使用情况")
    private OrderCouponAndActivityRsp orderPromotion;

    @ApiModelProperty(value = "操作者：用于更新产生者或更新者，一般传网关获取的ip")
    private String operator;
}
