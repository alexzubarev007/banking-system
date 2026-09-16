package controllers;

import accounts.AccountDto;
import accounts.requests.*;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.User;
import services.AccountService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/accounts")
@RequiredArgsConstructor
@Validated
public class AccountController {
    private final AccountService accountService;

    @PostMapping
    @Operation(summary = "Create account operation")
    @ResponseStatus(HttpStatus.CREATED)
    @ApiResponse(responseCode = "201", description = "Account created")
    @ApiResponse(responseCode = "403", description = "Forbidden")
    @ApiResponse(responseCode = "404", description = "User not found")
    @PreAuthorize("hasRole('CLIENT')")
    public AccountDto createAccount(@AuthenticationPrincipal User user) {
        return accountService.createAccount(user);
    }

    @GetMapping("/{accountId}")
    @Operation(summary = "Get account details operation")
    @ApiResponse(responseCode = "200", description = "Account details got")
    @ApiResponse(responseCode = "403", description = "Forbidden")
    @ApiResponse(responseCode = "404", description = "Account not found")
    @PreAuthorize("hasAnyRole('ADMIN', 'CLIENT')")
    public AccountDto getAccountDetails(@NotNull(message = "Account id is required")
                                        @PathVariable
                                        UUID accountId,
                                        @AuthenticationPrincipal User user) {
        GetAccountDetailsRequest request = new GetAccountDetailsRequest(accountId);
        return accountService.getAccountDetails(request, user);
    }

    @PatchMapping("/put")
    @Operation(summary = "Put money operation")
    @ApiResponse(responseCode = "200", description = "Money put")
    @ApiResponse(responseCode = "403", description = "Forbidden")
    @ApiResponse(responseCode = "404", description = "Account not found")
    @PreAuthorize("hasRole('CLIENT')")
    public AccountDto putMoney(@Valid @RequestBody PutMoneyRequest request,
                               @AuthenticationPrincipal User user) {
        return accountService.putMoney(request, user);
    }

    @PatchMapping("/withdraw")
    @Operation(summary = "Withdraw money operation")
    @ApiResponse(responseCode = "200", description = "Money withdrawn")
    @ApiResponse(responseCode = "400", description = "Not enough money")
    @ApiResponse(responseCode = "403", description = "Forbidden")
    @ApiResponse(responseCode = "404", description = "Account not found")
    @PreAuthorize("hasRole('CLIENT')")
    public AccountDto withdraw(@Valid @RequestBody WithdrawRequest request,
                               @AuthenticationPrincipal User user) {
        return accountService.withdraw(request, user);
    }

    @PatchMapping("/transfer")
    @Operation(summary = "Transfer money operation")
    @ApiResponse(responseCode = "200", description = "Money transferred")
    @ApiResponse(responseCode = "403", description = "Forbidden")
    @ApiResponse(responseCode = "404", description = "Account not found")
    @PreAuthorize("hasRole('CLIENT')")
    public void transfer(@Valid @RequestBody TransferRequest request,
                         @AuthenticationPrincipal User user) {
        accountService.transfer(request, user);
    }

    @GetMapping("users/{userId}")
    @Operation(summary = "Find accounts by user id operation")
    @ApiResponse(responseCode = "200", description = "Accounts found")
    @ApiResponse(responseCode = "403", description = "Forbidden")
    @PreAuthorize("hasRole('ADMIN')")
    public List<AccountDto> findByUserId(
            @NotNull(message = "User id is required")
            @PathVariable UUID userId,
           @AuthenticationPrincipal User user) {
        FindByUserIdRequest request = new FindByUserIdRequest((userId));
        return accountService.findByUserId(request, user);
    }

    @GetMapping("/all")
    @Operation(summary = "Find all accounts")
    @ApiResponse(responseCode = "200", description = "All accounts found")
    @ApiResponse(responseCode = "403", description = "Forbidden")
    @PreAuthorize("hasRole('ADMIN')")
    public List<AccountDto> findAll() {
        return accountService.findAll();
    }
}