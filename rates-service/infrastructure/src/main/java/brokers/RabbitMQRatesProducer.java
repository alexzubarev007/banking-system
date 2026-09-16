package brokers;

import lombok.RequiredArgsConstructor;
import messages.RatesMessage;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class RabbitMQRatesProducer implements services.QuotePublisher {

    @Value("${spring.rabbitmq.queues.rates}")
    private String queue;

    private final RabbitTemplate rabbitTemplate;

    public void publish(RatesMessage message) {
        rabbitTemplate.convertAndSend(queue, message);
    }
}
