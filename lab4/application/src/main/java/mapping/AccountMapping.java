package mapping;

import accounts.Account;
import accounts.AccountDto;

public class AccountMapping {
    public static AccountDto mapToDto(Account account) {
        return new AccountDto(
                account.getId(),
                account.getBalance(),
                account.getUserId());
    }
}
