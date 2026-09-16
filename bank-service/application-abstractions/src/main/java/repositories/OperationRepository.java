package repositories;

import operations.Operation;
import operations.OperationType;

import java.util.List;
import java.util.UUID;

public interface OperationRepository {
    Operation save(Operation operation);

    List<Operation> findByAccountId(UUID accountId);

    List<Operation> findByTypeAndAccountId(OperationType type, UUID accountId);

    void delete(UUID id);
}