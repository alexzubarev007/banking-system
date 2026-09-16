package services;

import messages.RatesMessage;

public interface QuotePublisher {
    void publish(RatesMessage quote);
}
