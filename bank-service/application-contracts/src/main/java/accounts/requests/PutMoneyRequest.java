package accounts.requests;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;
import java.util.UUID;

public record PutMoneyRequest(
        @Schema(description = "Account id")
        @NotNull(message = "Account id is required")
        UUID accountId,
        @Schema(description = "Money to put")
        @NotNull(message = "Money is required")
        @Positive(message = "Money must be positive")
        @jakarta.validation.constraints.Digits(integer = 17, fraction = 2)
        BigDecimal money) { }
