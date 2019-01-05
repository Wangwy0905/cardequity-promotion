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
}
