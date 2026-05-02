import accounts.AccountDto;
import controllers.AccountController;
import services.AccountService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.skyscreamer.jsonassert.JSONAssert;
import org.skyscreamer.jsonassert.JSONCompareMode;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class AccountControllerTests {

    private MockMvc mockMvc;

    @Mock
    private AccountService accountService;

    private AccountController accountController;
    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        accountController = new AccountController(accountService);
        mockMvc = MockMvcBuilders.standaloneSetup(accountController).build();
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
}