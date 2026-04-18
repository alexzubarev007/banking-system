import Controllers.OperationController;
import Operations.OperationDto;
import Operations.OperationType;
import Services.OperationService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.skyscreamer.jsonassert.JSONAssert;
import org.skyscreamer.jsonassert.JSONCompareMode;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class OperationControllerTests {

    private MockMvc mockMvc;

    @Mock
    private OperationService operationService;

    private OperationController operationController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        operationController = new OperationController(operationService);
        mockMvc = MockMvcBuilders.standaloneSetup(operationController).build();
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