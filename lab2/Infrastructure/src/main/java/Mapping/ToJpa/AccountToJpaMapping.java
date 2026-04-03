package Mapping.ToJpa;

import Accounts.Account;
import Entities.AccountJpaEntity;
import Entities.UserJpaEntity;
import jakarta.persistence.EntityManager;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public class AccountToJpaMapping {
    private EntityManager entityManager;

    public AccountJpaEntity mapToJpa(Account domainAccount) {
        UserJpaEntity user = entityManager.getReference(UserJpaEntity.class, domainAccount.getUserId());

        return new AccountJpaEntity(
                domainAccount.getId(),
                domainAccount.getBalance(),
                user);
    }
}