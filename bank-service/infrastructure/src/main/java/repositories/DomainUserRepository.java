package repositories;

import repositories.jpa.JpaUserRepository;
import users.Gender;
import users.User;
import entities.UserJpaEntity;
import mapping.todomain.UserToDomainMapping;
import mapping.tojpa.UserToJpaMapping;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
@RequiredArgsConstructor
public class DomainUserRepository
        implements UserRepository {

    private final UserToJpaMapping jpaMapper;
    private final JpaUserRepository jpaRepository;

    @Override
    public User save(User user) {
        UserJpaEntity userEntity = jpaMapper.mapToJpa(user);
        return UserToDomainMapping.mapToDomain(jpaRepository.save(userEntity));
    }

    @Override
    public Optional<User> findById(UUID id) {
        return jpaRepository.findById(id).map(UserToDomainMapping::mapToDomain);
    }

    @Override
    public void delete(UUID id) {
        jpaRepository.deleteById(id);
    }

    @Override
    public List<User> findFriendsById(UUID id) {
        return jpaRepository
                .findFriendsById(id)
                .stream()
                .map(UserToDomainMapping::mapToDomain)
                .toList();
    }

    public List<User> findByHairColorAndGender(String hairColor, Gender gender) {
        return jpaRepository
                .findByHairColorAndGender(hairColor, gender)
                .stream()
                .map(UserToDomainMapping::mapToDomain)
                .toList();
    }
}