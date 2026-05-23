package brokers;

import lombok.RequiredArgsConstructor;
import messages.RatesResponse;
import messages.RequestMessage;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class RabbitMQRequestProducer {
    @Value("${spring.rabbitmq.queues.requests}")
    private String queue;

    private final RabbitTemplate rabbitTemplate;

    public RatesResponse sendAndReceiveMessage(RequestMessage message)
            throws CurrencyProblemException {

        Object response = rabbitTemplate.convertSendAndReceive(queue, message);

        if (response instanceof RatesResponse ratesResponse && ratesResponse.status().equals("Ok")) {
            return ratesResponse;
        }

        throw new CurrencyProblemException("Can't get currency rate");
    }
}
