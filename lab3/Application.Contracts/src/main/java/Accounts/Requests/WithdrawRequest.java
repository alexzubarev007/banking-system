package Accounts.Requests;

import io.swagger.v3.oas.annotations.media.Schema;

import java.math.BigDecimal;
import java.util.UUID;

public record WithdrawRequest(
        @Schema(description = "Account id")
        UUID accountId,
        @Schema(description = "Money to withdraw")
        BigDecimal money) { }
