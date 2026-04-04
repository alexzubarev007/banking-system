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
import TransactionManager.TransactionManager;
import Users.User;
import lombok.AllArgsConstructor;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@AllArgsConstructor
public class AccountService {
    private TransactionManager transactionManager;
    private AccountRepository accountRepository;
    private UserRepository userRepository;
    private OperationRepository operationRepository;

    public AccountDto createAccount(CreateAccountRequest request)
            throws NotFoundException {
        transactionManager.begin();
        try {
            User user = userRepository
                    .findById(request.userId())
                    .orElseThrow(() -> new NotFoundException("User not found"));

            Account account = accountRepository
                    .add(new Account(null,BigDecimal.ZERO, user.getId()));

            transactionManager.commit();

            return AccountMapping.mapToDto(account);
        } catch (Exception exception) {
            transactionManager.rollback();
            throw exception;
        }
    }

    public AccountDto getAccountDetails(GetAccountDetailsRequest request)
            throws NotFoundException {
        Account account = accountRepository
                .findById(request.accountID())
                .orElseThrow(() -> new NotFoundException("User not found"));

        return AccountMapping.mapToDto(account);
    }

    public BigDecimal getBalance(GetBalanceRequest request)
            throws NotFoundException {
        Account account = accountRepository
                .findById(request.accountId())
                .orElseThrow(() -> new NotFoundException("Account not found"));

        return account.getBalance();
    }

    public AccountDto putMoney(PutMoneyRequest request)
            throws NotFoundException {
        transactionManager.begin();
        try {
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

            Account accountUpdated = accountRepository
                    .update(account);

            operationRepository.add(operation);

            transactionManager.commit();

            return AccountMapping.mapToDto(accountUpdated);
        } catch (Exception exception) {
            transactionManager.rollback();
            throw exception;
        }
    }

    public AccountDto withdraw(WithdrawRequest request)
            throws NotFoundException, NotEnoughMoneyException {
        transactionManager.begin();
        try {
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

            operationRepository.add(operation);

            Account accountUpdated = accountRepository
                    .update(account);

            transactionManager.commit();

            return AccountMapping.mapToDto(accountUpdated);
        } catch (Exception exception) {
            transactionManager.rollback();
            throw exception;
        }
    }

    public void transfer(TransferRequest request)
            throws NotFoundException, NotEnoughMoneyException {
        transactionManager.begin();
        try {
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

            operationRepository.add(senderOperation);
            operationRepository.add(recipientOperation);

            accountRepository.update(senderAccount);
            accountRepository.update(recipientAccount);

            transactionManager.commit();
        } catch (Exception exception) {
            transactionManager.rollback();
            throw exception;
        }
    }
}