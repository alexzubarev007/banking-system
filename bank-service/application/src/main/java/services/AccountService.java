package services;

import accounts.Account;
import accounts.AccountDto;
import accounts.requests.*;
import authentifications.Authentication;
import authentifications.Role;
import exceptions.NotEnoughMoneyException;
import mapping.AccountMapping;
import operations.Operation;
import operations.OperationType;
import repositories.AccountRepository;
import repositories.AuthentificationRepository;
import repositories.OperationRepository;
import repositories.UserRepository;
import services.exceptions.NotFoundException;
import services.exceptions.OtherDataException;
import services.exceptions.UnauthorizedException;
import users.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class AccountService {
    private final AccountRepository accountRepository;
    private final UserRepository userRepository;
    private final OperationRepository operationRepository;
    private final AuthentificationRepository authentificationRepository;

    @Transactional
    public AccountDto createAccount(org.springframework.security.core.userdetails.User userDetails)
            throws NotFoundException, UnauthorizedException {

        Authentication authentication = authentificationRepository
                .findByLogin(userDetails.getUsername())
                .orElseThrow(() -> new UnauthorizedException("Incorrect login"));

        User user = userRepository
                .findById(authentication.userId())
                .orElseThrow(() -> new NotFoundException("User not found"));

        Account account = accountRepository
                .save(new Account(null, BigDecimal.ZERO, user.getId()));

        return AccountMapping.mapToDto(account);
    }

    public AccountDto getAccountDetails(GetAccountDetailsRequest request,
                                        org.springframework.security.core.userdetails.User userDetails)
            throws NotFoundException, OtherDataException, UnauthorizedException {
        Authentication authentication = authentificationRepository
                .findByLogin(userDetails.getUsername())
                .orElseThrow(() -> new UnauthorizedException("Incorrect login"));
        Account account = accountRepository
                .findById(request.accountID())
                .orElseThrow(() -> new NotFoundException("User not found"));

        if ((authentication.role() == Role.CLIENT)
                && (!account.getUserId().equals(authentication.userId()))) {
            throw new OtherDataException("Try to read other data!");
        }

        return AccountMapping.mapToDto(account);
    }

    @Transactional
    public AccountDto putMoney(PutMoneyRequest request,
                               org.springframework.security.core.userdetails.User userDetails)
            throws NotFoundException, UnauthorizedException {
        Authentication authentication = authentificationRepository
                .findByLogin(userDetails.getUsername())
                .orElseThrow(() -> new UnauthorizedException("Incorrect login"));

        Account account = accountRepository
                .findByIdForUpdate(request.accountId())
                .orElseThrow(() -> new NotFoundException("Account not found"));

        if (!account.getUserId().equals(authentication.userId())) {
            throw new OtherDataException("Try to read other data!");
        }

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
    public AccountDto withdraw(WithdrawRequest request,
                               org.springframework.security.core.userdetails.User userDetails)
            throws NotFoundException,
            NotEnoughMoneyException,
            OtherDataException,
            UnauthorizedException {
        Authentication authentication = authentificationRepository
                .findByLogin(userDetails.getUsername())
                .orElseThrow(() -> new UnauthorizedException("Incorrect login"));

        Account account = accountRepository
                .findByIdForUpdate(request.accountId())
                .orElseThrow(() -> new NotFoundException("Account not found"));

        if (!account.getUserId().equals(authentication.userId())) {
            throw new OtherDataException("Try to read other data!");
        }

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
    public void transfer(TransferRequest request,
                         org.springframework.security.core.userdetails.User userDetails)
            throws NotFoundException,
            NotEnoughMoneyException,
            OtherDataException,
            UnauthorizedException {
        Authentication authentication = authentificationRepository
                .findByLogin(userDetails.getUsername())
                .orElseThrow(() -> new UnauthorizedException("Incorrect login"));

        Account.validateAmount(request.money());
        if (request.senderId().equals(request.recipientId())) {
            throw new IllegalArgumentException("Sender and recipient accounts must differ");
        }
        java.util.UUID firstId = request.senderId().compareTo(request.recipientId()) < 0
                ? request.senderId() : request.recipientId();
        java.util.UUID secondId = firstId.equals(request.senderId()) ? request.recipientId() : request.senderId();
        Account first = accountRepository.findByIdForUpdate(firstId)
                .orElseThrow(() -> new NotFoundException("Account not found"));
        Account second = accountRepository.findByIdForUpdate(secondId)
                .orElseThrow(() -> new NotFoundException("Account not found"));
        Account senderAccount = firstId.equals(request.senderId()) ? first : second;
        Account recipientAccount = firstId.equals(request.recipientId()) ? first : second;
        if (!senderAccount.getUserId().equals(authentication.userId())) {
            throw new OtherDataException("Try to read other data!");
        }

        User sender = userRepository
                .findById(senderAccount.getUserId())
                .orElseThrow(() -> new NotFoundException("Sender not found"));

        BigDecimal moneyToWithdraw;

        if (senderAccount.getUserId().equals(recipientAccount.getUserId())) {
            moneyToWithdraw = request.money();
        } else if (sender.getFriends().contains(recipientAccount.getUserId())) {
            moneyToWithdraw = request.money().multiply(BigDecimal.valueOf(1.03));
        } else {
            moneyToWithdraw = request.money().multiply(BigDecimal.valueOf(1.1));
        }

        moneyToWithdraw = moneyToWithdraw.setScale(2, java.math.RoundingMode.HALF_UP);
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

    public List<AccountDto> findByUserId(FindByUserIdRequest request,
                                         org.springframework.security.core.userdetails.User userDetails)
            throws OtherDataException,
            UnauthorizedException {
        Authentication authentication = authentificationRepository
                .findByLogin(userDetails.getUsername())
                .orElseThrow(() -> new UnauthorizedException("Incorrect login"));

        if (!request.userId().equals(authentication.userId())
                && authentication.role() == Role.CLIENT) {
            throw new OtherDataException("Try to read other data!");
        }

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