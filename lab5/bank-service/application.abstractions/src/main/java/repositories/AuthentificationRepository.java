package repositories;

import authentifications.Authentication;

import java.util.Optional;
import java.util.UUID;

public interface AuthentificationRepository {
    Authentication save(Authentication authentication);
    Optional<Authentication> findByLogin(String login);

    void delete(UUID id);
}
