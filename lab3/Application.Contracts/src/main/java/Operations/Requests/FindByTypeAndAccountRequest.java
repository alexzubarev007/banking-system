package Operations.Requests;

import Operations.OperationType;
import java.util.UUID;

public record FindByTypeAndAccountRequest(OperationType type, UUID accountId) { }
