package Accounts;

import java.math.BigDecimal;
import java.util.UUID;

public record AccountDto(UUID id,
                         BigDecimal balance,
                         UUID userId) { }