import Operations.OperationDto;
import Operations.OperationType;
import Runners.App;
import Services.OperationService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.skyscreamer.jsonassert.JSONAssert;
import org.skyscreamer.jsonassert.JSONCompareMode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(
        classes = App.class,
        properties = {
                "spring.autoconfigure.exclude=org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration",
                "spring.autoconfigure.exclude=org.springframework.boot.autoconfigure.orm.jpa.HibernateJpaAutoConfiguration"
        }
)
public class OperationControllerTests {
    @Autowired
    private WebApplicationContext webApplicationContext;

    private MockMvc mockMvc;

    @MockitoBean
    private OperationService operationService;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders
                .webAppContextSetup(webApplicationContext)
                .build();
    }

    @Test
    public void testGetHistory() throws Exception {
        UUID accountId = UUID.randomUUID();

        OperationDto testOperation1 = new OperationDto(
                UUID.randomUUID(),
                OperationType.PUT,
                LocalDateTime.now(),
                new BigDecimal(100),
                accountId
        );

        OperationDto testOperation2 = new OperationDto(
                UUID.randomUUID(),
                OperationType.WITHDRAW,
                LocalDateTime.now(),
                new BigDecimal(20),
                accountId
        );

        Mockito.when(operationService.getHistory(any()))
                .thenReturn(List.of(testOperation1, testOperation2));

        String resultJson = mockMvc
                .perform(get("/api/operation/{accountId}", accountId))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        JSONAssert.assertEquals("""
                [
                    { "type": "PUT", "money": 100 },
                    { "type": "WITHDRAW", "money": 20 }
                ]
                """, resultJson, JSONCompareMode.LENIENT);
    }

    @Test
    public void testFindByTypeAndAccount() throws Exception {
        UUID accountId = UUID.randomUUID();

        OperationDto testOperation = new OperationDto(
                UUID.randomUUID(),
                OperationType.PUT,
                LocalDateTime.now(),
                new BigDecimal(100),
                accountId
        );

        Mockito.when(operationService.findByTypeAndAccount(any()))
                .thenReturn(List.of(testOperation));

        String resultJson = mockMvc
                .perform(get("/api/operation/filters")
                        .param("type", "PUT")
                        .param("accountId", accountId.toString()))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        JSONAssert.assertEquals("""
                [
                    { "type": "PUT", "money": 100 }
                ]
                """, resultJson, JSONCompareMode.LENIENT);
    }
}