package Repositories;

import Operations.Operation;

import java.util.List;
import java.util.UUID;

public interface OperationRepository {
    Operation add(Operation operation);

    List<Operation> findByAccountId(UUID accountId);

    void delete(UUID id);
}