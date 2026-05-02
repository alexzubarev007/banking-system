package operations.requests;

import operations.OperationType;
import java.util.UUID;

public record FindByTypeAndAccountRequest(OperationType type, UUID accountId) { }
