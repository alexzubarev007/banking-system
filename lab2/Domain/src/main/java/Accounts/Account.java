package Accounts;

import Exceptions.NotEnoughMoneyException;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.math.BigDecimal;
import java.util.UUID;

@AllArgsConstructor
@Getter
public class Account {
    private final UUID id;
    private BigDecimal balance;
    private final UUID userId;

    public void putMoney(BigDecimal money) {
        balance = balance.add(money);
    }

    public void withdraw(BigDecimal money) throws NotEnoughMoneyException {
        if (balance.compareTo(money) < 0) {
            throw new NotEnoughMoneyException("There's not enough money to withdraw");
        }

        balance = balance.subtract(money);
    }
}