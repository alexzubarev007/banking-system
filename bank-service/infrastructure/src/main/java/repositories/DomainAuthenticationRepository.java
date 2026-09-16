package repositories;

import authentifications.Authentication;
import entities.AuthenticationJpaEntity;
import lombok.RequiredArgsConstructor;
import mapping.todomain.AuthenticationToDomainMapping;
import mapping.tojpa.AuthenticationToJpaMapping;
import org.springframework.stereotype.Repository;
import repositories.jpa.JpaAuthenticationRepository;

import java.util.Optional;
import java.util.UUID;

@Repository
@RequiredArgsConstructor
public class DomainAuthenticationRepository implements AuthentificationRepository{
    private final AuthenticationToJpaMapping mapper;
    private final JpaAuthenticationRepository jpaRepository;

    @Override
    public Authentication save(Authentication authentication) {
        AuthenticationJpaEntity authentificationEntity = mapper.mapToJpa(authentication);
        return AuthenticationToDomainMapping.mapToDomain(jpaRepository.save(authentificationEntity));
    }

    @Override
    public Optional<Authentication> findByLogin(String login) {
        return jpaRepository.findByLogin(login).map(AuthenticationToDomainMapping::mapToDomain);
    }

    @Override
    public void delete(UUID id) {
        jpaRepository.deleteById(id);
    }
}
