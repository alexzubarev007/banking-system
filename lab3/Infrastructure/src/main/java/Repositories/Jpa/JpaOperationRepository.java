package Repositories.Jpa;

import Entities.OperationJpaEntity;
import Operations.OperationType;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.UUID;

public interface JpaOperationRepository extends JpaRepository<OperationJpaEntity, UUID> {

    List<OperationJpaEntity> findByAccount_Id(UUID accountId);
    @EntityGraph(attributePaths = "account")
    @Query("""
            SELECT operation FROM OperationJpaEntity operation
            WHERE (COALESCE(:type, operation.type) = operation.type)
            AND (COALESCE(:accountId, operation.account.id) = operation.account.id)
            """
    )
    List<OperationJpaEntity> findByTypeAndAccountId(
            @Param("type") OperationType type,
            @Param("accountId") UUID accountId);
}
