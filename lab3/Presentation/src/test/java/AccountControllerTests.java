import Accounts.AccountDto;
import Runners.App;
import Services.AccountService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.skyscreamer.jsonassert.JSONAssert;
import org.skyscreamer.jsonassert.JSONCompareMode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(
        classes = App.class,
        properties = {
                "spring.autoconfigure.exclude=org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration,org.springframework.boot.autoconfigure.orm.jpa.HibernateJpaAutoConfiguration"
        }
)
public class AccountControllerTests {
    @Autowired
    private WebApplicationContext webApplicationContext;

    private MockMvc mockMvc;

    @MockitoBean
    private AccountService accountService;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders
                .webAppContextSetup(webApplicationContext)
                .build();
    }

    @Test
    public void testCreateAccount() throws Exception {
        UUID userId = UUID.randomUUID();

        AccountDto testAccount = new AccountDto(UUID.randomUUID(),
                BigDecimal.ZERO,
                userId);

        Mockito.when(accountService.createAccount(any())).thenReturn(testAccount);

        String requestJson = """
                {
                    "userId": "%s"
                }
                """.formatted(userId);

        String resultJson = mockMvc
                .perform(post("/api/accounts")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestJson))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString();

        JSONAssert.assertEquals("""
                {
                    "userId": "%s",
                    "balance": 0
                }
                """.formatted(userId), resultJson, JSONCompareMode.LENIENT);
    }

    @Test
    public void testPutMoney() throws Exception {
        UUID accountId = UUID.randomUUID();

        AccountDto testAccount = new AccountDto(accountId ,
                new BigDecimal(200),
                UUID.randomUUID());

        Mockito.when(accountService.putMoney(any()))
                .thenReturn(testAccount);

        String requestJson = """
                {
                    "accountId": "%s",
                    "money": 200
                }
                """.formatted(accountId);

        String resultJson = mockMvc
                .perform(patch("/api/accounts/put")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestJson))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        JSONAssert.assertEquals("""
                {
                    "balance": 200
                }
                """, resultJson, JSONCompareMode.LENIENT);
    }

    @Test
    public void testFindAll() throws Exception {
        AccountDto testAccount1 = new AccountDto(
                UUID.randomUUID(),
                new BigDecimal(239),
                UUID.randomUUID()
        );

        AccountDto testAccount2 = new AccountDto(
                UUID.randomUUID(),
                new BigDecimal(30),
                UUID.randomUUID()
        );

        Mockito.when(accountService.findAll())
                .thenReturn(List.of(testAccount1, testAccount2));

        String resultJson = mockMvc
                .perform(get("/api/accounts/all"))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        JSONAssert.assertEquals("""
                [
                    { "balance": 239 },
                    { "balance": 30 }
                ]
                """, resultJson, JSONCompareMode.LENIENT);
    }

    @Test
    public void testFindByUserId() throws Exception {
        UUID userId = UUID.randomUUID();

        AccountDto testAccount = new AccountDto(
                UUID.randomUUID(),
                new BigDecimal(67),
                userId
        );

        Mockito.when(accountService.findByUserId(any()))
                .thenReturn(List.of(testAccount));

        String resultJson = mockMvc
                .perform(get("/api/accounts/users/{userId}", userId))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        JSONAssert.assertEquals("""
            [
                {
                    "userId": "%s",
                    "balance": 67
                }
            ]
            """.formatted(userId), resultJson, JSONCompareMode.LENIENT);
    }
}