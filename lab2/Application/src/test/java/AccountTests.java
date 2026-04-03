import Accounts.Account;
import Accounts.Requests.TransferRequest;
import Accounts.AccountDto;
import Accounts.Requests.PutMoneyRequest;
import Accounts.Requests.WithdrawRequest;
import Exceptions.NotEnoughMoneyException;
import Repositories.AccountRepository;
import Repositories.OperationRepository;
import Repositories.UserRepository;
import Services.AccountService;
import TransactionManager.TransactionManager;
import Users.Gender;
import Users.User;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class AccountTests {
    @Mock
    AccountRepository accountRepository;

    @Mock
    UserRepository userRepository;

    @Mock
    OperationRepository operationRepository;

    @Mock
    TransactionManager transactionManager;


    @Test
    public void testPutMoney() {
        // arrange
        Account account = new Account(
                UUID.randomUUID(),
                BigDecimal.ZERO,
                UUID.randomUUID());

        when(accountRepository.findById(any())).thenReturn(Optional.of(account));
        when(accountRepository.update(any())).thenReturn(account);

        AccountService service = new AccountService(
                transactionManager,
                accountRepository,
                userRepository,
                operationRepository);
        PutMoneyRequest request1 = new PutMoneyRequest(account.getId(), new BigDecimal(500));
        PutMoneyRequest request2 = new PutMoneyRequest(account.getId(), new BigDecimal(1000));

        // act
        AccountDto accountUpdated1 = service.putMoney(request1);
        AccountDto accountUpdated2 = service.putMoney(request2);

        // assert
        assertEquals(new BigDecimal(500), accountUpdated1.balance());
        assertEquals(new BigDecimal(1500), accountUpdated2.balance());
    }

    @Test
    public void testWithdrawMoneySuccessfully() {
        // arrange
        Account account = new Account(
                UUID.randomUUID(),
                BigDecimal.ZERO,
                UUID.randomUUID());

        when(accountRepository.findById(any())).thenReturn(Optional.of(account));
        when(accountRepository.update(any())).thenReturn(account);

        AccountService service = new AccountService(
                transactionManager,
                accountRepository,
                userRepository,
                operationRepository);

        PutMoneyRequest putMoneyRequest = new PutMoneyRequest(account.getId(), new BigDecimal(500));
        WithdrawRequest withdrawRequest = new WithdrawRequest(account.getId(), new BigDecimal(200));

        // act
        AccountDto accountUpdated1 = service.putMoney(putMoneyRequest);
        AccountDto accountUpdated2 = service.withdraw(withdrawRequest);

        // assert
        assertEquals(new BigDecimal(500), accountUpdated1.balance());
        assertEquals(new BigDecimal(300), accountUpdated2.balance());
    }

    @Test
    public void testWithdrawMoneyUnsuccessfully() {
        // arrange
        Account account = new Account(
                UUID.randomUUID(),
                BigDecimal.ZERO,
                UUID.randomUUID());

        when(accountRepository.findById(any())).thenReturn(Optional.of(account));
        when(accountRepository.update(any())).thenReturn(account);

        AccountService service = new AccountService(
                transactionManager,
                accountRepository,
                userRepository,
                operationRepository);

        PutMoneyRequest putMoneyRequest = new PutMoneyRequest(account.getId(), new BigDecimal(500));
        WithdrawRequest withdrawRequest = new WithdrawRequest(account.getId(), new BigDecimal(600));

        // act
        AccountDto accountUpdated1 = service.putMoney(putMoneyRequest);

        // assert
        assertThrows(NotEnoughMoneyException.class, () -> {
            service.withdraw(withdrawRequest);
        });
    }

    @Test
    public void testSelfTransfer() throws Exception {
        // arrange
        User user = new User(
                UUID.randomUUID(),
                "Oleg",
                Gender.MALE,
                36,
                "brown");

        Account senderAccount = new Account(UUID.randomUUID(), BigDecimal.ZERO, user.getId());
        Account recipientAccount = new Account(UUID.randomUUID(), BigDecimal.ZERO, user.getId());

        senderAccount.putMoney(new BigDecimal(500));

        when(accountRepository.findById(senderAccount.getId()))
                .thenReturn(Optional.of(senderAccount));
        when(accountRepository.findById(recipientAccount.getId()))
                .thenReturn(Optional.of(recipientAccount));
        when(userRepository.findById(user.getId()))
                .thenReturn(Optional.of(user));

        AccountService service = new AccountService(
                transactionManager,
                accountRepository,
                userRepository,
                operationRepository);

        TransferRequest request = new TransferRequest(
                senderAccount.getId(),
                recipientAccount.getId(),
                new BigDecimal(100)
        );

        // act
        service.transfer(request);

        // assert
        assertTrue(new BigDecimal(400).compareTo(senderAccount.getBalance()) == 0);
        assertTrue(new BigDecimal(100).compareTo(recipientAccount.getBalance()) == 0);
    }

    @Test
    public void testTransferToFriend() throws Exception {
        // arrange
        User user1 = new User(
                UUID.randomUUID(),
                "Oleg",
                Gender.MALE,
                36,
                "brown");
        User user2 = new User(
                UUID.randomUUID(),
                "Artyom",
                Gender.MALE,
                29,
                "black");

        user1.getFriends().add(user2.getId());

        Account senderAccount = new Account(UUID.randomUUID(), BigDecimal.ZERO, user1.getId());
        Account recipientAccount = new Account(UUID.randomUUID(), BigDecimal.ZERO, user2.getId());

        senderAccount.putMoney(new BigDecimal(500));

        when(accountRepository.findById(senderAccount.getId()))
                .thenReturn(Optional.of(senderAccount));
        when(accountRepository.findById(recipientAccount.getId()))
                .thenReturn(Optional.of(recipientAccount));
        when(userRepository.findById(user1.getId()))
                .thenReturn(Optional.of(user1));

        AccountService service = new AccountService(
                transactionManager,
                accountRepository,
                userRepository,
                operationRepository);

        TransferRequest request = new TransferRequest(
                senderAccount.getId(),
                recipientAccount.getId(),
                new BigDecimal(100)
        );

        // act
        service.transfer(request);

        // assert
        assertTrue(new BigDecimal(397).compareTo(senderAccount.getBalance()) == 0);
        assertTrue(new BigDecimal(100).compareTo(recipientAccount.getBalance()) == 0);
    }

    @Test
    public void testTransferToNonFriend() throws Exception {
        // arrange
        User user1 = new User(
                UUID.randomUUID(),
                "Oleg",
                Gender.MALE,
                36,
                "brown");
        User user2 = new User(
                UUID.randomUUID(),
                "Artyom",
                Gender.MALE,
                29,
                "black");


        Account senderAccount = new Account(UUID.randomUUID(), BigDecimal.ZERO, user1.getId());
        Account recipientAccount = new Account(UUID.randomUUID(), BigDecimal.ZERO, user2.getId());

        senderAccount.putMoney(new BigDecimal(500));

        when(accountRepository.findById(senderAccount.getId()))
                .thenReturn(Optional.of(senderAccount));
        when(accountRepository.findById(recipientAccount.getId()))
                .thenReturn(Optional.of(recipientAccount));
        when(userRepository.findById(user1.getId()))
                .thenReturn(Optional.of(user1));

        AccountService service = new AccountService(
                transactionManager,
                accountRepository,
                userRepository,
                operationRepository);

        TransferRequest request = new TransferRequest(
                senderAccount.getId(),
                recipientAccount.getId(),
                new BigDecimal(100)
        );

        // act
        service.transfer(request);

        // assert
        assertTrue(new BigDecimal(390).compareTo(senderAccount.getBalance()) == 0);
        assertTrue(new BigDecimal(100).compareTo(recipientAccount.getBalance()) == 0);
    }

    @Test
    public void testTwoOperationSaved() {
        // arrange
        User user1 = new User(
                UUID.randomUUID(),
                "Oleg",
                Gender.MALE,
                36,
                "brown");
        User user2 = new User(
                UUID.randomUUID(),
                "Artyom",
                Gender.MALE,
                29,
                "black");


        Account senderAccount = new Account(UUID.randomUUID(), BigDecimal.ZERO, user1.getId());
        Account recipientAccount = new Account(UUID.randomUUID(), BigDecimal.ZERO, user2.getId());

        senderAccount.putMoney(new BigDecimal(500));

        when(accountRepository.findById(senderAccount.getId()))
                .thenReturn(Optional.of(senderAccount));
        when(accountRepository.findById(recipientAccount.getId()))
                .thenReturn(Optional.of(recipientAccount));
        when(userRepository.findById(user1.getId()))
                .thenReturn(Optional.of(user1));

        AccountService service = new AccountService(
                transactionManager,
                accountRepository,
                userRepository,
                operationRepository);

        TransferRequest request = new TransferRequest(
                senderAccount.getId(),
                recipientAccount.getId(),
                new BigDecimal(100)
        );

        // act
        service.transfer(request);

        // assert
        verify(operationRepository, times(2)).add(any());
    }
}
