package com.youyu.cardequity.promotion.biz.config.mq.consumer;

import com.youyu.cardequity.common.spring.mq.BaseRabbitConsumer;
import com.youyu.cardequity.common.spring.service.RabbitConsumerService;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.listener.SimpleMessageListenerContainer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author panqingqing
 * @version v1.0
 * @date 2019年04月25日 15:00:00
 * @work 活动发放用户优惠券消费者
 */
@Configuration
public class ActivityCouponAcquireConsumer extends BaseRabbitConsumer {

    @Autowired
    @Qualifier("activityCouponAcquireQueue")
    private Queue queue;

    @Autowired
    @Qualifier("activityCouponAcquireServiceImpl")
    private RabbitConsumerService rabbitConsumerService;

    @Override
    @Bean("activityCouponAcquireConsumerMessageContainer")
    public SimpleMessageListenerContainer consumerMessageContainer(ConnectionFactory connectionFactory) {
        return super.consumerMessageContainer(connectionFactory);
    }

    @Override
    protected RabbitConsumerService getRabbitConsumerService() {
        return rabbitConsumerService;
    }

    @Override
    protected Queue getQueue() {
        return queue;
    }
}
