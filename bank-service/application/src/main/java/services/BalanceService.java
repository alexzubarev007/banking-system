package services;

import accounts.Account;
import authentifications.Authentication;
import authentifications.Role;
import balances.requests.GetBalanceRequest;
import balances.requests.GetConvertedBalanceRequest;
import brokers.*;
import lombok.RequiredArgsConstructor;
import messages.RatesMessage;
import messages.RatesResponse;
import messages.RequestMessage;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Service;
import repositories.AccountRepository;
import repositories.AuthentificationRepository;
import services.exceptions.NotFoundException;
import services.exceptions.OtherDataException;
import services.exceptions.UnauthorizedException;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class BalanceService {
    private final AccountRepository accountRepository;
    private final AuthentificationRepository authentificationRepository;
    private final RabbitMQRequestProducer producer;
    private final Map<String, BigDecimal> ratesCache = new HashMap<>();

    public BigDecimal getBalance(GetBalanceRequest request,
                                 User userDetails)
            throws NotFoundException, UnauthorizedException, OtherDataException {

        Authentication authentication = authentificationRepository
                .findByLogin(userDetails.getUsername())
                .orElseThrow(() -> new UnauthorizedException("Incorrect login"));
        Account account = accountRepository
                .findById(request.accountId())
                .orElseThrow(() -> new NotFoundException("User not found"));

        if ((authentication.role() == Role.CLIENT)
                && (!account.getUserId().equals(authentication.userId()))) {
            throw new OtherDataException("Try to read other data!");
        }

        return account.getBalance();
    }

    public BigDecimal getConvertedBalance(GetConvertedBalanceRequest request,
                                          User userDetails)
            throws NotFoundException, CurrencyProblemException,
            OtherDataException, UnauthorizedException {

        String code = request.code();
        Authentication authentication = authentificationRepository
                .findByLogin(userDetails.getUsername())
                .orElseThrow(() -> new UnauthorizedException("Incorrect login"));

        Account account = accountRepository
                .findById(request.accountId())
                .orElseThrow(() -> new NotFoundException("Account not found"));

        if (!ratesCache.containsKey(code)) {
            RatesResponse response = producer.sendAndReceiveMessage(new RequestMessage(code));
            ratesCache.put(code, response.rate());
        }

        if ((authentication.role() == Role.CLIENT)
                && (!account.getUserId().equals(authentication.userId()))) {
            throw new OtherDataException("Try to read other data!");
        }

        return account.getBalance().divide(ratesCache.get(code), 5, RoundingMode.HALF_UP);
    }

    @RabbitListener(queues = "${spring.rabbitmq.queues.rates}")
    public void consume(RatesMessage message) {
        ratesCache.put(message.code(), message.rate());
    }
}
