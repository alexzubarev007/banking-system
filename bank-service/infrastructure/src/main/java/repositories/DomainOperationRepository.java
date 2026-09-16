package repositories;

import operations.Operation;
import entities.OperationJpaEntity;
import mapping.todomain.OperationToDomainMapping;
import mapping.tojpa.OperationToJpaMapping;
import operations.OperationType;
import repositories.jpa.JpaOperationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
@RequiredArgsConstructor
public class DomainOperationRepository
        implements OperationRepository {

    private final OperationToJpaMapping jpaMapper;
    private final JpaOperationRepository jpaRepository;
    @Override
    public Operation save(Operation operation) {
        OperationJpaEntity operationEntity = jpaMapper.mapToJpa(operation);
        jpaRepository.save(operationEntity);
        return OperationToDomainMapping.mapToDomain(operationEntity);
    }

    @Override
    public List<Operation> findByAccountId(UUID accountId) {
        return jpaRepository.findByAccount_Id(accountId)
                .stream()
                .map(OperationToDomainMapping::mapToDomain)
                .toList();
    }

    public List<Operation> findByTypeAndAccountId(OperationType type, UUID accountId) {
        return jpaRepository
                .findByTypeAndAccountId(type, accountId)
                .stream()
                .map(OperationToDomainMapping::mapToDomain)
                .toList();
    }

    @Override
    public void delete(UUID id) {
       jpaRepository.deleteById(id);
    }
}