package brokers;

import messages.*;
import rates.*;
import org.junit.jupiter.api.Test;
import java.math.BigDecimal;
import java.time.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class CachedRateProviderTest {
    Instant now = Instant.parse("2026-09-16T10:00:00Z");
    RabbitMQRequestProducer producer = mock(RabbitMQRequestProducer.class);
    CachedRateProvider cache =
            new CachedRateProvider(
                    producer, Duration.ofSeconds(30), Clock.fixed(now, ZoneOffset.UTC));

    RatesMessage message(String value, long age) {
        return new RatesMessage("USD", new BigDecimal(value), now.minusSeconds(age));
    }

    @Test
    void ignoresOutOfOrderEvents() {
        cache.consume(message("90", 1));
        cache.consume(message("80", 10));
        assertEquals(new BigDecimal("90"), cache.getQuote("USD").rate());
        verifyNoInteractions(producer);
    }

    @Test
    void refreshesExpiredQuote() {
        cache.consume(message("80", 31));
        when(producer.request("USD"))
                .thenReturn(new CurrencyQuote("USD", new BigDecimal("91"), now));
        assertEquals(new BigDecimal("91"), cache.getQuote("USD").rate());
        verify(producer).request("USD");
    }

    @Test
    void unavailableProviderDoesNotReturnStaleQuote() {
        cache.consume(message("80", 31));
        when(producer.request("USD")).thenThrow(new CurrencyProblemException("offline"));
        assertThrows(CurrencyProblemException.class, () -> cache.getQuote("USD"));
    }

    @Test
    void rejectsMalformedAndStaleReplies() {
        assertThrows(IllegalArgumentException.class, () -> cache.getQuote("US"));
        when(producer.request("USD"))
                .thenReturn(new CurrencyQuote("USD", BigDecimal.ONE, now.minusSeconds(31)));
        assertThrows(CurrencyProblemException.class, () -> cache.getQuote("USD"));
    }

    @Test
    void rubDoesNotUseBroker() {
        assertEquals(BigDecimal.ONE, cache.getQuote("RUB").rate());
        verifyNoInteractions(producer);
    }

    @Test
    void concurrentEventsRetainNewestQuote() throws Exception {
        try (var pool = java.util.concurrent.Executors.newVirtualThreadPerTaskExecutor()) {
            var tasks = new java.util.ArrayList<java.util.concurrent.Future<?>>();
            for (int age = 0; age < 100; age++) {
                final int n = age;
                tasks.add(pool.submit(() -> cache.consume(message(Integer.toString(100 - n), n))));
            }
            for (var task : tasks) task.get();
        }
        assertEquals(new BigDecimal("100"), cache.getQuote("USD").rate());
    }
}
