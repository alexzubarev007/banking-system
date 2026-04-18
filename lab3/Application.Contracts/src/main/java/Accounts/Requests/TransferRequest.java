package Accounts.Requests;

import io.swagger.v3.oas.annotations.media.Schema;

import java.math.BigDecimal;
import java.util.UUID;

public record TransferRequest(
        @Schema(description = "Sender id")
        UUID senderId,
        @Schema(description = "Recipient id")
        UUID recipientId,
        @Schema(description = "Money to transfer")
        BigDecimal money) {
}
