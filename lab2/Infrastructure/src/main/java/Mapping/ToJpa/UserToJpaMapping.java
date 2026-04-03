package Mapping.ToJpa;

import Entities.UserJpaEntity;
import Users.User;
import jakarta.persistence.EntityManager;
import lombok.AllArgsConstructor;

import java.util.Set;
import java.util.stream.Collectors;

@AllArgsConstructor
public class UserToJpaMapping {
    private EntityManager entityManager;

    public UserJpaEntity mapToJpa(User domainUser) {
        Set<UserJpaEntity> friends = domainUser
                .getFriends()
                .stream()
                .map(id -> entityManager.getReference(UserJpaEntity.class, id))
                .collect(Collectors.toSet());
        UserJpaEntity user = new UserJpaEntity(
                domainUser.getId(),
                domainUser.getName(),
                domainUser.getGender(),
                friends,
                domainUser.getAge(),
                domainUser.getHairColor());

        user.setFriends(friends);

        return user;
    }
}