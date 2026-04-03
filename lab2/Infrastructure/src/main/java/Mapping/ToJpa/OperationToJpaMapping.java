package Mapping.ToJpa;

import Entities.AccountJpaEntity;
import Entities.OperationJpaEntity;
import Operations.Operation;
import jakarta.persistence.EntityManager;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public class OperationToJpaMapping {
    private EntityManager entityManager;

    public OperationJpaEntity mapToJpa(Operation operation) {
        AccountJpaEntity account = entityManager.getReference(AccountJpaEntity.class, operation.accountId());

        return new OperationJpaEntity(
                operation.id(),
                operation.type(),
                operation.money(),
                operation.time(),
                account);
    }
}