package Mapping.ToDomain;

import Accounts.Account;
import Entities.AccountJpaEntity;

public class AccountToDomainMapping {
    public static Account mapToDomain(AccountJpaEntity accountEntity) {
        return new Account(
                accountEntity.getId(),
                accountEntity.getBalance(),
                accountEntity.getUser().getId());
    }
}
