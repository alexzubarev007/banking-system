package Accounts.Requests;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;
import java.util.UUID;

public record WithdrawRequest(
        @Schema(description = "Account id")
        @NotNull(message = "Account id is required")
        UUID accountId,
        @Schema(description = "Money to withdraw")
        @Positive(message = "Money must be positive")
        BigDecimal money) { }
