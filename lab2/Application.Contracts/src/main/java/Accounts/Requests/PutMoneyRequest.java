package Accounts.Requests;

import java.math.BigDecimal;
import java.util.UUID;

public record PutMoneyRequest(UUID accountId,
                              BigDecimal money) { }
