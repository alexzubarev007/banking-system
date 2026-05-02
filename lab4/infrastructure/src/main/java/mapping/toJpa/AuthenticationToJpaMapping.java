package mapping.toJpa;

import authentifications.Authentication;
import entities.AuthenticationJpaEntity;
import entities.UserJpaEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import repositories.jpa.JpaUserRepository;

@Component
@RequiredArgsConstructor
public class AuthenticationToJpaMapping {
    private final JpaUserRepository repository;

    public AuthenticationJpaEntity mapToJpa(Authentication authentication) {
        UserJpaEntity user = (authentication.userId() != null)
                ? repository.getReferenceById(authentication.userId())
                : null;

        return new AuthenticationJpaEntity(
                authentication.id(),
                authentication.login(),
                authentication.passwordHash(),
                authentication.role(),
                user);
    }
}
