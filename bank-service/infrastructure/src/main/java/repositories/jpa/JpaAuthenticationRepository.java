package repositories.jpa;

import entities.AuthenticationJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface JpaAuthenticationRepository
        extends JpaRepository<AuthenticationJpaEntity, UUID> {
    Optional<AuthenticationJpaEntity> findByLogin(String login);
}
