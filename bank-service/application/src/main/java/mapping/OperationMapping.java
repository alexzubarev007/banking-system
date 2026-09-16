package mapping;

import operations.Operation;
import operations.OperationDto;

public class OperationMapping {
    public static OperationDto mapToDto(Operation operation) {
        return new OperationDto(
                operation.id(),
                operation.type(),
                operation.time(),
                operation.money(),
                operation.accountId());
    }
}