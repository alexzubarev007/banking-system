package Repositories;

import Accounts.Account;
import Entities.AccountJpaEntity;
import Mapping.ToDomain.AccountToDomainMapping;
import Mapping.ToJpa.AccountToJpaMapping;
import Repositories.Jpa.JpaAccountRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
@Repository
@RequiredArgsConstructor
public class DomainAccountRepository
        implements AccountRepository {

    private final AccountToJpaMapping jpaMapper;
    private final JpaAccountRepository jpaRepository;
    @Override
    public Account save(Account account) {
        AccountJpaEntity accountEntity = jpaMapper.mapToJpa(account);
        jpaRepository.save(accountEntity);
        return AccountToDomainMapping.mapToDomain(accountEntity);
    }

    @Override
    public Optional<Account> findById(UUID id) {
        return jpaRepository.findById(id).map(AccountToDomainMapping::mapToDomain);
    }

    @Override
    public List<Account> findByUserId(UUID userId) {
        return jpaRepository
                .findByUser_Id(userId)
                .stream()
                .map(AccountToDomainMapping::mapToDomain)
                .toList();
    }

    @Override
    public List<Account> findAll() {
        return jpaRepository
                .findAll()
                .stream()
                .map(AccountToDomainMapping::mapToDomain)
                .toList();
    }

    @Override
    public void delete(UUID id) {
        jpaRepository.deleteById(id);
    }
}