package Repositories;

import Operations.Operation;
import Entities.OperationJpaEntity;
import Mapping.ToDomain.OperationToDomainMapping;
import Mapping.ToJpa.OperationToJpaMapping;
import jakarta.persistence.EntityManager;

import java.util.List;
import java.util.UUID;

public class OperationJpaRepository
        implements OperationRepository {

    private final EntityManager entityManager;
    private final OperationToJpaMapping jpaMapper;

    public OperationJpaRepository(EntityManager entityManager) {
        this.entityManager = entityManager;
        this.jpaMapper = new OperationToJpaMapping(entityManager);
    }

    @Override
    public Operation add(Operation Operation) {
        OperationJpaEntity operationEntity = jpaMapper.mapToJpa(Operation);
        entityManager.persist(operationEntity);
        return OperationToDomainMapping.mapToDomain(operationEntity);
    }

    @Override
    public List<Operation> findByAccountId(UUID accountId) {
        return entityManager
                .createQuery(
                        "SELECT operation FROM OperationJpaEntity operation WHERE operation.account.id = :accountId",
                        OperationJpaEntity.class)
                .setParameter("accountId", accountId)
                .getResultList()
                .stream()
                .map(OperationToDomainMapping::mapToDomain)
                .toList();
    }

    @Override
    public void delete(UUID id) {
        OperationJpaEntity operationEntity = entityManager.getReference(OperationJpaEntity.class, id);
        if (operationEntity != null) {
            entityManager.remove(operationEntity);
        }
    }
}