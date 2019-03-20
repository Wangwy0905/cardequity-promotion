package com.youyu.cardequity.promotion.dto.other.message;

import lombok.Data;

/**
 * 通知权益释放消息体
 */
@Data
public class OrderCouponMessageDto {
    /**
     * 客户编号
     */
    private String clientId;

    /**
     * 订单编号
     */
    private String orderId;

    /**
     * 消息类型
     */
    private String messageType;
}
