import controllers.*;
import services.*;
import repositories.*;
import rates.RateProvider;
import authentifications.*;
import accounts.Account;
import users.Gender;
import securityconfigs.SecurityConfig;
import handlers.GlobalExceptionHandler;
import org.junit.jupiter.api.Test;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.context.ContextConfiguration;
import java.util.*;
import java.math.BigDecimal;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(
        controllers = {
            AccountController.class,
            UserController.class,
            OperationController.class,
            BalanceController.class
        })
@ContextConfiguration(
        classes = {
            AccountController.class,
            UserController.class,
            OperationController.class,
            BalanceController.class
        })
@Import({
    SecurityConfig.class,
    AccountService.class,
    UserService.class,
    OperationService.class,
    BalanceService.class,
    GlobalExceptionHandler.class
})
class SecurityRegressionTest {
    @Autowired MockMvc mvc;
    @MockitoBean AccountRepository accounts;
    @MockitoBean UserRepository users;
    @MockitoBean OperationRepository operations;
    @MockitoBean AuthentificationRepository auth;
    @MockitoBean RateProvider rates;
    UUID owner = UUID.randomUUID();

    void login() {
        when(auth.findByLogin("alice"))
                .thenReturn(
                        Optional.of(
                                new Authentication(
                                        UUID.randomUUID(), "alice", "x", Role.CLIENT, owner)));
    }

    @Test
    void loginPageRemainsAvailable() throws Exception {
        mvc.perform(get("/login")).andExpect(status().isOk());
    }

    @Test
    void anonymousCannotReadAccounts() throws Exception {
        mvc.perform(get("/api/accounts/all")).andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser(username = "alice", roles = "CLIENT")
    void clientCannotUseAdminEndpoint() throws Exception {
        mvc.perform(get("/api/accounts/all")).andExpect(status().isForbidden());
        verifyNoInteractions(accounts);
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void adminCanListAccounts() throws Exception {
        when(accounts.findAll()).thenReturn(List.of());
        mvc.perform(get("/api/accounts/all")).andExpect(status().isOk());
    }

    @Test
    @WithMockUser(username = "alice", roles = "CLIENT")
    void cannotReadOtherFriends() throws Exception {
        login();
        mvc.perform(get("/api/users/friends/" + UUID.randomUUID()))
                .andExpect(status().isForbidden());
        verifyNoInteractions(users);
    }

    @Test
    @WithMockUser(username = "alice", roles = "CLIENT")
    void canReadOwnHistoryWithDistinctUuidObjects() throws Exception {
        login();
        UUID id = UUID.randomUUID();
        when(accounts.findById(id))
                .thenReturn(
                        Optional.of(
                                new Account(
                                        id, BigDecimal.TEN, UUID.fromString(owner.toString()))));
        when(operations.findByAccountId(id)).thenReturn(List.of());
        mvc.perform(get("/api/operation/" + id)).andExpect(status().isOk());
    }

    @Test
    @WithMockUser(username = "alice", roles = "CLIENT")
    void cannotReadOtherAccount() throws Exception {
        login();
        UUID id = UUID.randomUUID();
        when(accounts.findById(id))
                .thenReturn(Optional.of(new Account(id, BigDecimal.TEN, UUID.randomUUID())));
        mvc.perform(get("/api/accounts/" + id)).andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(username = "alice", roles = "CLIENT")
    void rejectsNegativeTransferAtBoundary() throws Exception {
        mvc.perform(
                        patch("/api/accounts/transfer")
                                .contentType("application/json")
                                .content(
                                        "{\"senderId\":\""
                                                + UUID.randomUUID()
                                                + "\",\"recipientId\":\""
                                                + UUID.randomUUID()
                                                + "\",\"money\":-100}"))
                .andExpect(status().isBadRequest());
        verifyNoInteractions(accounts);
    }
}
