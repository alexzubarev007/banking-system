package Controllers;

import Accounts.AccountDto;
import Accounts.Requests.*;
import Services.AccountService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/accounts")
@RequiredArgsConstructor
public class AccountController {
    private final AccountService accountService;

    @PostMapping
    @Operation(summary = "Create account operation")
    @ResponseStatus(HttpStatus.CREATED)
    @ApiResponse(responseCode = "201", description = "Account created")
    @ApiResponse(responseCode = "404", description = "User not found")
    public AccountDto createAccount(@RequestBody CreateAccountRequest request) {
        return accountService.createAccount(request);
    }

    @GetMapping("/{accountId}")
    @Operation(summary = "Get account details operation")
    @ApiResponse(responseCode = "200", description = "Account details got")
    @ApiResponse(responseCode = "404", description = "Account not found")
    public AccountDto getAccountDetails(@PathVariable UUID accountId) {
        GetAccountDetailsRequest request = new GetAccountDetailsRequest(accountId);
        return accountService.getAccountDetails(request);
    }

    @PatchMapping("/put")
    @Operation(summary = "Put money operation")
    @ApiResponse(responseCode = "200", description = "Money put")
    @ApiResponse(responseCode = "404", description = "Account not found")
    public AccountDto putMoney(@RequestBody PutMoneyRequest request) {
        return accountService.putMoney(request);
    }

    @PatchMapping("/withdraw")
    @Operation(summary = "Withdraw money operation")
    @ApiResponse(responseCode = "200", description = "Money withdrawn")
    @ApiResponse(responseCode = "404", description = "Account not found")
    @ApiResponse(responseCode = "400", description = "Not enough money")
    public AccountDto withdraw(@RequestBody WithdrawRequest request) {
        return accountService.withdraw(request);
    }

    @PatchMapping("/transfer")
    @Operation(summary = "Transfer money operation")
    @ApiResponse(responseCode = "200", description = "Money transferred")
    @ApiResponse(responseCode = "404", description = "Account not found")
    public void transfer(@RequestBody TransferRequest request) {
        accountService.transfer(request);
    }

    @GetMapping("users/{userId}")
    @Operation(summary = "Find accounts by user id operation")
    @ApiResponse(responseCode = "200", description = "Accounts found")
    public List<AccountDto> findByUserId(@PathVariable UUID userId) {
        FindByUserIdRequest request = new FindByUserIdRequest((userId));
        return accountService.findByUserId(request);
    }

    @GetMapping("/all")
    @Operation(summary = "Find all accounts")
    @ApiResponse(responseCode = "200", description = "All accounts found")
    public List<AccountDto> findAll() {
        return accountService.findAll();
    }
}