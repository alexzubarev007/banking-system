package Accounts.Requests;

import java.math.BigDecimal;
import java.util.UUID;

public record WithdrawRequest(UUID accountId,
                              BigDecimal money) { }
