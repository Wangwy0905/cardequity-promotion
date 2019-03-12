package com.youyu.cardequity.promotion.biz.config.mq.consumer;

import com.youyu.cardequity.promotion.biz.service.ClientTakeInCouponService;
import com.youyu.cardequity.promotion.dto.other.CommonBoolDto;
import com.youyu.cardequity.promotion.dto.other.message.OrderCouponMessageDto;
import com.youyu.cardequity.promotion.vo.req.BaseOrderInPromotionReq;
import com.youyu.common.exception.BizException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.AcknowledgeMode;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.ChannelAwareMessageListener;
import org.springframework.amqp.rabbit.listener.SimpleMessageListenerContainer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import static com.alibaba.fastjson.JSON.parseObject;


/**
 * 订单占用权益释放
 */
@Slf4j
@Configuration
public class OrderCouponReleaseConsumer {
    @Autowired
    private ConnectionFactory connectionFactory;

    @Autowired
    private ClientTakeInCouponService clientTakeInCouponService;


    /**
     * 订单下单释放权益消息:Queue
     */
    @Autowired
    @Qualifier("orderCouponQueue")
    private Queue orderCouponQueue;

    @Value("${rabbit.order.coupon.concurrent.consumers:8}")
    private int orderCouponConcurrentConsumers;

    @Value("${rabbit.order.coupon.max.concurrent.consumers:16}")
    private int orderCouponMaxConcurrentConsumers;


    @Bean
    public SimpleMessageListenerContainer orderExpireMessageContainer() {

        SimpleMessageListenerContainer simpleMessageListenerContainer = new SimpleMessageListenerContainer(connectionFactory);
        simpleMessageListenerContainer.setQueues(orderCouponQueue);
        simpleMessageListenerContainer.setExposeListenerChannel(true);
        simpleMessageListenerContainer.setConcurrentConsumers(orderCouponConcurrentConsumers);
        simpleMessageListenerContainer.setMaxConcurrentConsumers(orderCouponMaxConcurrentConsumers);
        simpleMessageListenerContainer.setAcknowledgeMode(AcknowledgeMode.MANUAL);

        simpleMessageListenerContainer.setMessageListener((ChannelAwareMessageListener) (message, channel) -> {
            try {
                // 订单超时消息
                OrderCouponMessageDto couponMessage = parseObject(new String(message.getBody()), OrderCouponMessageDto.class);
                log.info("OrderCouponConsumer.consumer() Message: {}", couponMessage);
                try {

                    // 处理释放消息
                    handleExpireMessage(couponMessage);
                } catch (BizException e) {
                    // 业务异常时不进行重试
                    log.error("OrderExpireConsumer.consumer() got biz exception. {}", e);
                }
                // 消息消费成功
                channel.basicAck(message.getMessageProperties().getDeliveryTag(), false);
            } catch (Exception e) {
                log.error("OrderExpireConsumer.consumer() got exception. {}", e);
                // 消息消费失败,重新投递消息
                channel.basicReject(message.getMessageProperties().getDeliveryTag(), true);
            }
        });

        return simpleMessageListenerContainer;
    }

    private void handleExpireMessage(OrderCouponMessageDto couponMessage) {
        try{
            BaseOrderInPromotionReq req=new BaseOrderInPromotionReq();
            req.setClientId(couponMessage.getClientId());
            req.setOrderId(couponMessage.getOrderId());
            CommonBoolDto boolDto = clientTakeInCouponService.cancelOrderCouponAndActivityDeal(req);
            if (!boolDto.getSuccess()){
                log.info(String.format("客户%s，订单%s优惠券释放失败，失败原因：%s",couponMessage.getClientId(),couponMessage.getOrderId(),boolDto.getDesc()));
            }
        }catch (Exception ex){
            log.info(String.format("客户%s，订单%s优惠券释放失败，失败原因：%s",couponMessage.getClientId(),couponMessage.getOrderId(),ex.getMessage()));
            throw ex;
        }
    }
}
