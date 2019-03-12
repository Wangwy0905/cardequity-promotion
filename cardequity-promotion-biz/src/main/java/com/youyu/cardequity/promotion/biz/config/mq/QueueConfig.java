package com.youyu.cardequity.promotion.biz.config.mq;

import org.springframework.amqp.core.Queue;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Created by caiyi on 2019/3/11.
 */
@Configuration
public class QueueConfig {
    /**
     * 订单优惠券(解冻)消息:Queue
     */
    @Value("${rabbit.orderCouponQueue}")
    private String orderCouponQueue;

    /**
     * 订单优惠券(解冻)通知Queue
     */
    @Bean("orderCouponQueue")
    public Queue orderCouponQueue() {
        return new Queue(orderCouponQueue, true);
    }
}
