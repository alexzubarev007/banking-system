package Mapping.ToJpa;

import Entities.AccountJpaEntity;
import Entities.OperationJpaEntity;
import Operations.Operation;
import Repositories.Jpa.JpaAccountRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class OperationToJpaMapping {
    private final JpaAccountRepository jpaAccountRepository;


    public OperationJpaEntity mapToJpa(Operation operation) {
        AccountJpaEntity account = jpaAccountRepository.getReferenceById(operation.accountId());

        return new OperationJpaEntity(
                operation.id(),
                operation.type(),
                operation.money(),
                operation.time(),
                account);
    }
}