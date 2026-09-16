package accounts;

import exceptions.NotEnoughMoneyException;
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

    public static void validateAmount(BigDecimal money) {
        if (money == null || money.signum() <= 0 || money.stripTrailingZeros().scale() > 2) {
            throw new IllegalArgumentException("Amount must be positive, with at most two decimal places");
        }
    }

    public void putMoney(BigDecimal money) {
        validateAmount(money);
        balance = balance.add(money);
    }

    public void withdraw(BigDecimal money) throws NotEnoughMoneyException {
        validateAmount(money);
        if (balance.compareTo(money) < 0) {
            throw new NotEnoughMoneyException("There's not enough money to withdraw");
        }

        balance = balance.subtract(money);
    }
}