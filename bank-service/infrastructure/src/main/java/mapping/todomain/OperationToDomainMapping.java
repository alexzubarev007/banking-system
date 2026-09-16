package mapping.todomain;

import entities.OperationJpaEntity;
import operations.Operation;

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
