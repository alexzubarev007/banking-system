package brokers;

import org.springframework.amqp.core.Queue;
import org.springframework.amqp.support.converter.JacksonJsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {
    @Value("${spring.rabbitmq.queues.rates}")
    private String rateQueue;

    @Value("${spring.rabbitmq.queues.requests}")
    private String requestQueue;

    @Bean
    public MessageConverter messageConverter() {
        return new JacksonJsonMessageConverter("messages");
    }

    @Bean
    public Queue rateQueue() {
        return new Queue(rateQueue);
    }

    @Bean
    public Queue requestQueue() {
        return new Queue(requestQueue);
    }
}
