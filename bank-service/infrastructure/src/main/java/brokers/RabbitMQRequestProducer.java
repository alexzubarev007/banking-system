package brokers;
import lombok.RequiredArgsConstructor;
import messages.*;
import rates.*;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
@Component
@RequiredArgsConstructor
public class RabbitMQRequestProducer {
    @Value("${spring.rabbitmq.queues.requests}") private String queue;
    private final RabbitTemplate rabbitTemplate;
    public CurrencyQuote request(String code) {
        try {
            Object reply = rabbitTemplate.convertSendAndReceive(queue, new RequestMessage(code));
            if (reply instanceof RatesResponse r && "Ok".equals(r.status()) && code.equals(r.currencyCode())) {
                return new CurrencyQuote(code, r.rate(), r.time());
            }
        } catch (org.springframework.amqp.AmqpException | IllegalArgumentException e) {
            throw new CurrencyProblemException("Currency provider unavailable or invalid reply", e);
        }
        throw new CurrencyProblemException("Unknown currency or currency provider unavailable");
    }
}
