package Repositories;

import Users.User;
import Entities.UserJpaEntity;
import Mapping.ToDomain.UserToDomainMapping;
import Mapping.ToJpa.UserToJpaMapping;
import jakarta.persistence.EntityManager;

import java.util.Optional;
import java.util.UUID;

public class UserJpaRepository
        implements UserRepository {

    private final EntityManager entityManager;
    private final UserToJpaMapping jpaMapper;

    public UserJpaRepository(EntityManager entityManager) {
        this.entityManager = entityManager;
        this.jpaMapper = new UserToJpaMapping(entityManager);
    }

    @Override
    public User add(User User) {
        UserJpaEntity UserEntity = jpaMapper.mapToJpa(User);
        entityManager.persist(UserEntity);
        return UserToDomainMapping.mapToDomain(UserEntity);
    }

    @Override
    public Optional<User> findById(UUID id) {
        return Optional.ofNullable(entityManager.find(UserJpaEntity.class, id))
                .map(UserToDomainMapping::mapToDomain);
    }

    @Override
    public User update(User User) {
        UserJpaEntity userEntity = jpaMapper.mapToJpa(User);
        return UserToDomainMapping.mapToDomain(entityManager.merge(userEntity));
    }

    @Override
    public void delete(UUID id) {
        UserJpaEntity userEntity = entityManager.getReference(UserJpaEntity.class, id);
        if (userEntity != null) {
            entityManager.remove(userEntity);
        }
    }
}