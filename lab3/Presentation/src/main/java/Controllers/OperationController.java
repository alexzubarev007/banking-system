package Controllers;

import Operations.OperationDto;
import Operations.OperationType;
import Operations.Requests.FindByTypeAndAccountRequest;
import Operations.Requests.GetHistoryRequest;
import Services.OperationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/operation")
@RequiredArgsConstructor
@Validated
public class OperationController {
    private final OperationService operationService;

    @GetMapping("/{accountId}")
    @Operation(summary = "Get history operation")
    @ApiResponse(responseCode = "200", description = "Operation history got")
    @ApiResponse(responseCode = "404", description = "Account not found")
    public List<OperationDto> getHistory(@NotNull(message = "Account id is required")
                                         @PathVariable UUID accountId) {
        GetHistoryRequest request = new GetHistoryRequest(accountId);
        return operationService.getHistory(request);
    }

    @GetMapping("/filters")
    @Operation(summary = "Find by type and account operation")
    @ApiResponse(responseCode = "200", description = "Operations got")
    public List<OperationDto> findByTypeAndAccount(@RequestParam(required = false) OperationType type,
                                                   @RequestParam(required = false) UUID accountId) {
        FindByTypeAndAccountRequest request = new FindByTypeAndAccountRequest(type, accountId);
        return operationService.findByTypeAndAccount(request);
    }
}