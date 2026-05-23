package mapping.toDomain;

import accounts.Account;
import entities.AccountJpaEntity;

public class AccountToDomainMapping {
    public static Account mapToDomain(AccountJpaEntity accountEntity) {
        return new Account(
                accountEntity.getId(),
                accountEntity.getBalance(),
                accountEntity.getUser().getId());
    }
}
