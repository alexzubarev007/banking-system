package Mapping;

import Operations.Operation;
import Operations.OperationDto;

public class OperationMapping {
    public static OperationDto mapToDto(Operation operation) {
        return new OperationDto(
                operation.id(),
                operation.type(),
                operation.time(),
                operation.money(),
                operation.id());
    }
}