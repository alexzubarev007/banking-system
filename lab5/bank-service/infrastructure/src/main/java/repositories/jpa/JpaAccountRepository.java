package repositories.jpa;

import entities.AccountJpaEntity;
import org.jspecify.annotations.NullMarked;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;


import java.util.List;
import java.util.UUID;


public interface JpaAccountRepository extends JpaRepository<AccountJpaEntity, UUID> {
    List<AccountJpaEntity> findByUser_Id(UUID userId);

    @EntityGraph(attributePaths = "user")
    @NullMarked
    List<AccountJpaEntity> findAll();
}
