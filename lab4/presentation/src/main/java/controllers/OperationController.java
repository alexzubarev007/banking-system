package controllers;

import authentifications.Authentication;
import operations.OperationDto;
import operations.OperationType;
import operations.requests.FindByTypeAndAccountRequest;
import operations.requests.GetHistoryRequest;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.User;
import services.OperationService;
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
    @PreAuthorize("hasAnyRole('ADMIN', 'CLIENT')")
    public List<OperationDto> getHistory(@NotNull(message = "Account id is required")
                                         @PathVariable UUID accountId,
                                         @AuthenticationPrincipal User user) {
        GetHistoryRequest request = new GetHistoryRequest(accountId);
        return operationService.getHistory(request, user);
    }

    @GetMapping("/filters")
    @Operation(summary = "Find by type and account operation")
    @ApiResponse(responseCode = "200", description = "Operations got")
    @PreAuthorize("hasRole('ADMIN')")
    public List<OperationDto> findByTypeAndAccount(@RequestParam(required = false) OperationType type,
                                                   @RequestParam(required = false) UUID accountId) {
        FindByTypeAndAccountRequest request = new FindByTypeAndAccountRequest(type, accountId);
        return operationService.findByTypeAndAccount(request);
    }
}