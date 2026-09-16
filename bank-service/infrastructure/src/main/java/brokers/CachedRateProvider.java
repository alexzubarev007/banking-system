package brokers;

import rates.*;
import messages.RatesMessage;
import java.time.*;
import java.util.concurrent.ConcurrentHashMap;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class CachedRateProvider implements RateProvider {
    private final ConcurrentHashMap<String, CurrencyQuote> cache = new ConcurrentHashMap<>();
    private final RabbitMQRequestProducer producer;
    private final Duration ttl;
    private final Clock clock;

    @Autowired
    public CachedRateProvider(
            RabbitMQRequestProducer producer, @Value("${rates.cache-ttl:30s}") Duration ttl) {
        this(producer, ttl, Clock.systemUTC());
    }

    public CachedRateProvider(RabbitMQRequestProducer producer, Duration ttl, Clock clock) {
        if (ttl.isNegative() || ttl.isZero())
            throw new IllegalArgumentException("TTL must be positive");
        this.producer = producer;
        this.ttl = ttl;
        this.clock = clock;
    }

    @Override
    public CurrencyQuote getQuote(String code) {
        if (code == null || !code.matches("[A-Z]{3}"))
            throw new IllegalArgumentException("Currency must have three uppercase letters");
        if (code.equals("RUB"))
            return new CurrencyQuote("RUB", java.math.BigDecimal.ONE, clock.instant());
        return cache.compute(
                code,
                (key, existing) -> {
                    if (fresh(existing)) return existing;
                    CurrencyQuote fetched = producer.request(key);
                    if (!key.equals(fetched.code()) || !fresh(fetched))
                        throw new CurrencyProblemException("No fresh currency quote");
                    return existing != null && existing.time().isAfter(fetched.time())
                            ? existing
                            : fetched;
                });
    }

    private boolean fresh(CurrencyQuote quote) {
        Instant now = clock.instant();
        return quote != null && !quote.time().isAfter(now) && quote.time().plus(ttl).isAfter(now);
    }

    @RabbitListener(queues = "${spring.rabbitmq.queues.rates}")
    public void consume(RatesMessage message) {
        CurrencyQuote quote = new CurrencyQuote(message.code(), message.rate(), message.time());
        if (quote.time().isAfter(clock.instant())) return;
        cache.merge(
                quote.code(),
                quote,
                (old, update) -> update.time().isAfter(old.time()) ? update : old);
    }
}
