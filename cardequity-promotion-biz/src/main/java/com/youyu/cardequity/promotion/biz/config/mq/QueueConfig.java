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
    @Value("${rabbit.orderCouponQueue:rabbit_order_coupon_queue}")
    private String orderCouponQueue;

    /**
     * 订单优惠券(解冻)通知Queue
     */
    @Bean("orderCouponQueue")
    public Queue orderCouponQueue() {
        return new Queue(orderCouponQueue, true);
    }

    /**
     * 优惠券活动发放queue name
     */
    @Value("${rabbit.activityCouponAcquireQueue:rabbit_activity_coupon_acquire_queue}")
    private String activityCouponAcquireQueue;

    /**
     * 优惠券活动发放queue
     *
     * @return
     */
    @Bean("activityCouponAcquireQueue")
    public Queue activityCouponAcquireQueue() {
        return new Queue(activityCouponAcquireQueue, true);
    }
}
