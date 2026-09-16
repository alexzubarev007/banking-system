package services;

import messages.*;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import java.util.ArrayList;

class RatesServiceTest {
    @Test
    void publishesPositiveTimestampedQuotesAndReplies() {
        var events = new ArrayList<RatesMessage>();
        var service = new RatesService(events::add);
        service.changeRates();
        assertEquals(3, events.size());
        for (var event : events) {
            assertTrue(event.rate().signum() > 0);
            assertNotNull(event.time());
            var reply = service.getRate(new RequestMessage(event.code()));
            assertEquals(event.rate(), reply.rate());
            assertEquals(event.time(), reply.time());
        }
        assertEquals("Unknown currency", service.getRate(new RequestMessage("ZZZ")).status());
    }
}
