package services;

import accounts.*;
import accounts.requests.*;
import authentifications.*;
import repositories.*;
import users.Gender;
import org.junit.jupiter.api.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import java.math.BigDecimal;
import java.util.*;
import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

class AccountServiceTest {
    AccountRepository accounts = mock(AccountRepository.class);
    UserRepository users = mock(UserRepository.class);
    OperationRepository operations = mock(OperationRepository.class);
    AuthentificationRepository auth = mock(AuthentificationRepository.class);
    AccountService service = new AccountService(accounts, users, operations, auth);
    UUID owner = UUID.randomUUID(), senderId = UUID.randomUUID(), recipientId = UUID.randomUUID();
    org.springframework.security.core.userdetails.User principal =
            new org.springframework.security.core.userdetails.User("alice", "x", List.of());

    @BeforeEach
    void setup() {
        when(auth.findByLogin("alice"))
                .thenReturn(
                        Optional.of(
                                new Authentication(
                                        UUID.randomUUID(), "alice", "x", Role.CLIENT, owner)));
    }

    @ParameterizedTest
    @CsvSource({"self,100.00", "friend,103.00", "other,110.00"})
    void commissionAndOrderedLocks(String relation, String debit) {
        UUID recipientOwner =
                relation.equals("self") ? UUID.fromString(owner.toString()) : UUID.randomUUID();
        Account from = new Account(senderId, new BigDecimal("500.00"), owner);
        Account to = new Account(recipientId, BigDecimal.ZERO, recipientOwner);
        users.User sender = new users.User(owner, "Alice", Gender.FEMALE, 22, "black");
        if (relation.equals("friend")) sender.addFriend(recipientOwner);
        when(accounts.findByIdForUpdate(senderId)).thenReturn(Optional.of(from));
        when(accounts.findByIdForUpdate(recipientId)).thenReturn(Optional.of(to));
        when(users.findById(owner)).thenReturn(Optional.of(sender));
        service.transfer(
                new TransferRequest(senderId, recipientId, new BigDecimal("100.00")), principal);
        assertEquals(
                0,
                from.getBalance()
                        .compareTo(new BigDecimal("500.00").subtract(new BigDecimal(debit))));
        assertEquals(0, to.getBalance().compareTo(new BigDecimal("100.00")));
        var order = inOrder(accounts);
        order.verify(accounts)
                .findByIdForUpdate(senderId.compareTo(recipientId) < 0 ? senderId : recipientId);
        order.verify(accounts)
                .findByIdForUpdate(senderId.compareTo(recipientId) < 0 ? recipientId : senderId);
        verify(operations, times(2)).save(any());
        verify(accounts).save(from);
        verify(accounts).save(to);
    }

    @Test
    void invalidAmountsAndSameAccountDoNotWrite() {
        for (BigDecimal amount :
                Arrays.asList(null, BigDecimal.ZERO, new BigDecimal("-1"), new BigDecimal("0.001")))
            assertThrows(
                    IllegalArgumentException.class,
                    () ->
                            service.transfer(
                                    new TransferRequest(senderId, recipientId, amount), principal));
        assertThrows(
                IllegalArgumentException.class,
                () ->
                        service.transfer(
                                new TransferRequest(senderId, senderId, BigDecimal.ONE),
                                principal));
        verifyNoInteractions(accounts, operations);
    }

    @Test
    void insufficientBalanceDoesNotSave() {
        when(accounts.findByIdForUpdate(senderId))
                .thenReturn(Optional.of(new Account(senderId, BigDecimal.ZERO, owner)));
        when(accounts.findByIdForUpdate(recipientId))
                .thenReturn(Optional.of(new Account(recipientId, BigDecimal.ZERO, owner)));
        when(users.findById(owner))
                .thenReturn(
                        Optional.of(new users.User(owner, "Alice", Gender.FEMALE, 22, "black")));
        assertThrows(
                exceptions.NotEnoughMoneyException.class,
                () ->
                        service.transfer(
                                new TransferRequest(senderId, recipientId, BigDecimal.ONE),
                                principal));
        verifyNoInteractions(operations);
        verify(accounts, never()).save(any());
    }

    @Test
    void domainRejectsNegativeDepositsAndWithdrawals() {
        Account account = new Account(senderId, BigDecimal.TEN, owner);
        assertThrows(
                IllegalArgumentException.class, () -> account.putMoney(BigDecimal.ONE.negate()));
        assertThrows(IllegalArgumentException.class, () -> account.withdraw(null));
        assertEquals(BigDecimal.TEN, account.getBalance());
    }
}
