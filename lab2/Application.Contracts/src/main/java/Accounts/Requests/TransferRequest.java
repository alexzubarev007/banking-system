package Accounts.Requests;

import java.math.BigDecimal;
import java.util.UUID;

public record TransferRequest(UUID senderId,
                              UUID recipientId,
                              BigDecimal money) { }
