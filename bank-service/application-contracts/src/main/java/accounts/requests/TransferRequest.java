package accounts.requests;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.util.UUID;

public record TransferRequest(
        @Schema(description = "Sender id")
        @NotNull(message = "Sender id is required")
        UUID senderId,
        @Schema(description = "Recipient id")
        @NotNull(message = "Recipient id is required")
        UUID recipientId,
        @Schema(description = "Money to transfer")
        @NotNull(message = "Money is required")
        @jakarta.validation.constraints.Positive
        @jakarta.validation.constraints.Digits(integer = 17, fraction = 2)
        BigDecimal money) {
}
