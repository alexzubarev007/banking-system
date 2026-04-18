package Services;

import Accounts.Account;
import Accounts.AccountDto;
import Accounts.Requests.*;
import Exceptions.NotEnoughMoneyException;
import Mapping.AccountMapping;
import Operations.Operation;
import Operations.OperationType;
import Repositories.AccountRepository;
import Repositories.OperationRepository;
import Repositories.UserRepository;
import Services.Exceptions.NotFoundException;
import Users.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AccountService {
    private final AccountRepository accountRepository;
    private final UserRepository userRepository;
    private final OperationRepository operationRepository;

    @Transactional
    public AccountDto createAccount(CreateAccountRequest request)
            throws NotFoundException {
        User user = userRepository
                .findById(request.userId())
                .orElseThrow(() -> new NotFoundException("User not found"));

        Account account = accountRepository
                .save(new Account(null, BigDecimal.ZERO, user.getId()));

        return AccountMapping.mapToDto(account);
    }

    public AccountDto getAccountDetails(GetAccountDetailsRequest request)
            throws NotFoundException {
        Account account = accountRepository
                .findById(request.accountID())
                .orElseThrow(() -> new NotFoundException("User not found"));

        return AccountMapping.mapToDto(account);
    }

    @Transactional
    public AccountDto putMoney(PutMoneyRequest request)
            throws NotFoundException {
        Account account = accountRepository
                .findById(request.accountId())
                .orElseThrow(() -> new NotFoundException("Account not found"));

        account.putMoney(request.money());

        Operation operation = new Operation(
                null,
                OperationType.PUT,
                request.money(),
                LocalDateTime.now(),
                account.getId());

        Account accountUpdated = accountRepository.save(account);

        operationRepository.save(operation);

        return AccountMapping.mapToDto(accountUpdated);
    }

    @Transactional
    public AccountDto withdraw(WithdrawRequest request)
            throws NotFoundException, NotEnoughMoneyException {
        Account account = accountRepository
                .findById(request.accountId())
                .orElseThrow(() -> new NotFoundException("Account not found"));

        account.withdraw(request.money());

        Operation operation = new Operation(
                null,
                OperationType.WITHDRAW,
                request.money(),
                LocalDateTime.now(),
                account.getId());

        operationRepository.save(operation);

        Account accountUpdated = accountRepository.save(account);

        return AccountMapping.mapToDto(accountUpdated);
    }

    @Transactional
    public void transfer(TransferRequest request)
            throws NotFoundException, NotEnoughMoneyException {
        Account senderAccount = accountRepository
                .findById(request.senderId())
                .orElseThrow(() -> new NotFoundException("Sender account not found"));

        Account recipientAccount = accountRepository
                .findById(request.recipientId())
                .orElseThrow(() -> new NotFoundException("Recipient account not found"));

        User sender = userRepository
                .findById(senderAccount.getUserId())
                .orElseThrow(() -> new NotFoundException("Sender not found"));

        BigDecimal moneyToWithdraw;

        if (senderAccount.getUserId() == recipientAccount.getUserId()) {
            moneyToWithdraw = request.money();
        } else if (sender.getFriends().contains(recipientAccount.getUserId())) {
            moneyToWithdraw = request.money().multiply(BigDecimal.valueOf(1.03));
        } else {
            moneyToWithdraw = request.money().multiply(BigDecimal.valueOf(1.1));
        }

        senderAccount.withdraw(moneyToWithdraw);
        recipientAccount.putMoney(request.money());

        LocalDateTime time = LocalDateTime.now();

        Operation senderOperation = new Operation(
                null,
                OperationType.WITHDRAW,
                moneyToWithdraw,
                time,
                senderAccount.getId());

        Operation recipientOperation = new Operation(
                null,
                OperationType.PUT,
                request.money(),
                time,
                recipientAccount.getId());

        operationRepository.save(senderOperation);
        operationRepository.save(recipientOperation);

        accountRepository.save(senderAccount);
        accountRepository.save(recipientAccount);
    }

    public List<AccountDto> findByUserId(FindByUserIdRequest request) {
        return accountRepository
                .findByUserId(request.userId())
                .stream()
                .map(AccountMapping::mapToDto)
                .toList();
    }

    public List<AccountDto> findAll() {
        return accountRepository
                .findAll()
                .stream()
                .map(AccountMapping::mapToDto)
                .toList();
    }
}