package repositories.jpa;

import entities.AccountJpaEntity;
import org.jspecify.annotations.NullMarked;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;


import java.util.List;
import java.util.UUID;


public interface JpaAccountRepository extends JpaRepository<AccountJpaEntity, UUID> {
    @org.springframework.data.jpa.repository.Lock(jakarta.persistence.LockModeType.PESSIMISTIC_WRITE)
    @org.springframework.data.jpa.repository.Query("select a from AccountJpaEntity a where a.id = :id")
    java.util.Optional<AccountJpaEntity> findByIdForUpdate(@org.springframework.data.repository.query.Param("id") UUID id);

    List<AccountJpaEntity> findByUser_Id(UUID userId);

    @EntityGraph(attributePaths = "user")
    @NullMarked
    List<AccountJpaEntity> findAll();
}
