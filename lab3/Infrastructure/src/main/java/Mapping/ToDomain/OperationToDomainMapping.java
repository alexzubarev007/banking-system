package Mapping.ToDomain;

import Entities.OperationJpaEntity;
import Operations.Operation;

public class OperationToDomainMapping {

    public static Operation mapToDomain(OperationJpaEntity operationEntity) {
        return new Operation(
                operationEntity.getId(),
                operationEntity.getType(),
                operationEntity.getMoney(),
                operationEntity.getTime(),
                operationEntity.getAccount().getId());
    }
}
