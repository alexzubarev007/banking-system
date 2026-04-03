package Mapping;

import Accounts.Account;
import Accounts.AccountDto;

public class AccountMapping {
    public static AccountDto mapToDto(Account account) {
        return new AccountDto(
                account.getId(),
                account.getBalance(),
                account.getUserId());
    }
}
