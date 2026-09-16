package rates;

public interface RateProvider {
    CurrencyQuote getQuote(String code);
}
