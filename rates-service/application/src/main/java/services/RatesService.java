package services;

import brokers.RabbitMQRatesProducer;
import lombok.RequiredArgsConstructor;
import messages.RatesMessage;
import messages.RatesResponse;
import messages.RequestMessage;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.Map;
import java.util.Random;

@Service
@RequiredArgsConstructor
@EnableScheduling
public class RatesService {
    @Value("#{${rates}}")
    private Map<String, BigDecimal> rates;

    private final RabbitMQRatesProducer producer;

    @Scheduled(fixedRate = 10000)
    public void changeRates() {
        rates.forEach((code, rate) -> {
            double randomChange = new Random().nextDouble(11) - 5;
            BigDecimal updated = rate.add(BigDecimal.valueOf(randomChange));

            BigDecimal positiveRate = updated.max(BigDecimal.valueOf(0.000001));

            rates.put(code, positiveRate);

            RatesMessage message = new RatesMessage(code, positiveRate, Instant.now());

            producer.sendMessage(message);
        });
    }

    @RabbitListener(queues = "${spring.rabbitmq.queues.requests}")
    public RatesResponse getRate(RequestMessage message) {
        BigDecimal rate = rates.get(message.code());

        if (!rates.containsKey(message.code())) {
            return new RatesResponse(message.code(), null, "No such currency");
        }

        return new RatesResponse(message.code(), rate, "Ok");
    }
}
