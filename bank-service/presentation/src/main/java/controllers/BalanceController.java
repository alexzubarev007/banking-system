package controllers;

import balances.requests.GetBalanceRequest;
import balances.requests.GetConvertedBalanceRequest;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.validation.constraints.Size;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.User;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import services.BalanceService;

import java.math.BigDecimal;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@Validated
public class BalanceController {
    private final BalanceService balanceService;

    @GetMapping("/api/accounts/{accountId}/balance")
    @Operation(summary = "Get balance operation")
    @ApiResponse(responseCode = "200", description = "Got balance")
    @ApiResponse(responseCode = "403", description = "Forbidden")
    @ApiResponse(responseCode = "404", description = "Account not found")
    @PreAuthorize("hasAnyRole('ADMIN', 'CLIENT')")
    public BigDecimal getBalance(@PathVariable UUID accountId,
                                 @Size(min = 3, max = 3)
                                 @RequestParam(required = false) String currencyCode,
                                 @AuthenticationPrincipal User user) {
        if (currencyCode == null) {
            GetBalanceRequest request = new GetBalanceRequest(accountId);
            return balanceService.getBalance(request, user);
        }

        GetConvertedBalanceRequest request = new GetConvertedBalanceRequest(currencyCode, accountId);

        return balanceService.getConvertedBalance(request, user);
    }
}
