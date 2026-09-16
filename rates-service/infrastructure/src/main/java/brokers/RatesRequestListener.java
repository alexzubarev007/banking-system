package brokers;

import messages.*;
import services.RatesService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.amqp.rabbit.annotation.RabbitListener;

@Component
@RequiredArgsConstructor
public class RatesRequestListener {
    private final RatesService rates;

    @RabbitListener(queues = "${spring.rabbitmq.queues.requests}")
    public RatesResponse receive(RequestMessage request) {
        return rates.getRate(request);
    }
}
