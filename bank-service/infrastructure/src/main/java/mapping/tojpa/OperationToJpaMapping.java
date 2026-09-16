package mapping.tojpa;

import entities.AccountJpaEntity;
import entities.OperationJpaEntity;
import operations.Operation;
import repositories.jpa.JpaAccountRepository;
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