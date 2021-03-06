package com.rabbitmq.study.consume;

import com.rabbitmq.client.Channel;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * mq 消费者监听类 所有消费者必须实现
 */
public abstract class AbstractMessageListener implements MessageListenerService {

    @Autowired
    Jackson2JsonMessageConverter messageConverter;

    /**
     * 所有消费必须重写此方法
     * @param message
     * @param messageConverter
     */
    public abstract void receiveMessage(Message message, MessageConverter messageConverter);

    @Override
    public void onMessage(Message message, Channel channel) throws Exception {

        MessageProperties messageProperties = message.getMessageProperties();

        Long deliveryTag = messageProperties.getDeliveryTag();

      /*  Long consumerCount = redisTemplate.opsForHash().increment(MQConstants.MQ_CONSUMER_RETRY_COUNT_KEY,
                messageProperties.getMessageId(), 1);*/

        //logger.debug("消息di:{},消费次数:{}",messageProperties.getMessageId(),consumerCount);
         //开始执行业务

        try {
            receiveMessage(message, messageConverter);
            // 成功的回执
            channel.basicAck(deliveryTag, false);
            // 如果消费成功，将Redis中统计消息消费次数的缓存删除

        } catch (Exception e) {
           // logger.error("RabbitMQ 消息消费失败，" + e.getMessage(), e);
            //if (consumerCount >= MQConstants.MAX_CONSUMER_COUNT) {
                // 入死信队列
                //channel.basicReject(deliveryTag, false);
           // } else {
                // 重回到队列，重新消费,按照2的指数级递增
               // Thread.sleep((long) (Math.pow(MQConstants.BASE_NUM, consumerCount)*1000));
               // channel.basicNack(deliveryTag, false, true);
            //}
            return;
        }
        /** 删除相应的key */
     /*   try {
            redisTemplate.opsForHash().delete(MQConstants.MQ_CONSUMER_RETRY_COUNT_KEY,
                    messageProperties.getMessageId());
        } catch(Exception e) {
            LogUtil.info(logger,"消息监听redis删除消费异常",()-> e);
        }*/
    }
}
