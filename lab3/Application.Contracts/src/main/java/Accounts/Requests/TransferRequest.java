package Accounts.Requests;

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
        BigDecimal money) {
}
