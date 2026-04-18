package Repositories.Jpa;

import Entities.AccountJpaEntity;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;


import java.util.List;
import java.util.UUID;


public interface JpaAccountRepository extends JpaRepository<AccountJpaEntity, UUID> {
    List<AccountJpaEntity> findByUser_Id(UUID userId);

    @Override
    @EntityGraph(attributePaths = "user")
    List<AccountJpaEntity> findAll();
}
