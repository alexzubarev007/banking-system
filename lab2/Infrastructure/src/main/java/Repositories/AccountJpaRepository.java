package Repositories;

import Accounts.Account;
import Entities.AccountJpaEntity;
import Mapping.ToDomain.AccountToDomainMapping;
import Mapping.ToJpa.AccountToJpaMapping;
import jakarta.persistence.EntityManager;

import java.util.Optional;
import java.util.UUID;

public class AccountJpaRepository
        implements AccountRepository {

    private final EntityManager entityManager;
    private final AccountToJpaMapping jpaMapper;

    public AccountJpaRepository(EntityManager entityManager) {
        this.entityManager = entityManager;
        this.jpaMapper = new AccountToJpaMapping(entityManager);
    }

    @Override
    public Account add(Account account) {
        AccountJpaEntity accountEntity = jpaMapper.mapToJpa(account);
        entityManager.persist(accountEntity);
        return AccountToDomainMapping.mapToDomain(accountEntity);
    }

    @Override
    public Optional<Account> findById(UUID id) {
        return Optional.ofNullable(entityManager.find(AccountJpaEntity.class, id))
                .map(AccountToDomainMapping::mapToDomain);
    }

    @Override
    public Account update(Account account) {
        AccountJpaEntity accountEntity = jpaMapper.mapToJpa(account);
        return AccountToDomainMapping.mapToDomain(entityManager.merge(accountEntity));
    }

    @Override
    public void delete(UUID id) {
        AccountJpaEntity accountEntity = entityManager.getReference(AccountJpaEntity.class, id);
        if (accountEntity != null) {
            entityManager.remove(accountEntity);
        }
    }
}