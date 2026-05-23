package balances.requests;

import java.util.UUID;

public record GetConvertedBalanceRequest(
        String code,
        UUID accountId) {
}
