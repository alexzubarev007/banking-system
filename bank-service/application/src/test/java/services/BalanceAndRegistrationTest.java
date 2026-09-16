package services;

import accounts.Account;
import authentifications.*;
import balances.requests.GetConvertedBalanceRequest;
import rates.*;
import repositories.*;
import registrations.*;
import org.junit.jupiter.api.Test;
import java.util.*;
import java.math.BigDecimal;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class BalanceAndRegistrationTest {
    @Test
    void checksOwnerBeforeContactingBroker() {
        var accounts = mock(AccountRepository.class);
        var auth = mock(AuthentificationRepository.class);
        var rates = mock(RateProvider.class);
        UUID owner = UUID.randomUUID(), account = UUID.randomUUID();
        when(auth.findByLogin("alice"))
                .thenReturn(
                        Optional.of(
                                new Authentication(
                                        UUID.randomUUID(), "alice", "x", Role.CLIENT, owner)));
        when(accounts.findById(account))
                .thenReturn(Optional.of(new Account(account, BigDecimal.TEN, UUID.randomUUID())));
        var service = new BalanceService(accounts, auth, rates);
        var user = new org.springframework.security.core.userdetails.User("alice", "x", List.of());
        assertThrows(
                services.exceptions.OtherDataException.class,
                () ->
                        service.getConvertedBalance(
                                new GetConvertedBalanceRequest("USD", account), user));
        verifyNoInteractions(rates);
    }

    @Test
    void registrationHashesPasswordBeforePersistence() {
        var accounts = mock(AuthentificationRepository.class);
        var users = mock(UserRepository.class);
        var encoder = new org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder();
        var service = new RegistrationService(accounts, users, encoder);
        service.registerAdmin(new RegisterAdminRequest("admin", "a-long-test-password"));
        var capture = org.mockito.ArgumentCaptor.forClass(Authentication.class);
        verify(accounts).save(capture.capture());
        assertNotEquals("a-long-test-password", capture.getValue().passwordHash());
        assertTrue(encoder.matches("a-long-test-password", capture.getValue().passwordHash()));
    }
}
