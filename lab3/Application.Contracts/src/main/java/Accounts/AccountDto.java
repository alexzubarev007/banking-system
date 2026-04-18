package Accounts;

import io.swagger.v3.oas.annotations.media.Schema;

import java.math.BigDecimal;
import java.util.UUID;

public record AccountDto(
        @Schema(description = "Account id")
        UUID id,
        @Schema(description = "Account balance")
        BigDecimal balance,
        @Schema(description = "User id")
        UUID userId) { }