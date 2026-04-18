package Repositories;

import Accounts.Account;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface AccountRepository {
    Account save(Account account);

    Optional<Account> findById(UUID id);

    List<Account> findByUserId(UUID id);

    List<Account> findAll();

    void delete(UUID id);
}
