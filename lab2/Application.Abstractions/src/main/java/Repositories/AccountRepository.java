package Repositories;

import Accounts.Account;

import java.util.Optional;
import java.util.UUID;

public interface AccountRepository {
    Account add(Account account);

    Optional<Account> findById(UUID id);

    Account update(Account account);

    void delete(UUID id);
}
