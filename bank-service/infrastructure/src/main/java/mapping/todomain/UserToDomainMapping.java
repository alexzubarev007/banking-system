package mapping.todomain;

import entities.UserJpaEntity;
import users.User;

import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

public class UserToDomainMapping {

    public static User mapToDomain(UserJpaEntity userEntity) {
        User user = new User(
                userEntity.getId(),
                userEntity.getName(),
                userEntity.getGender(),
                userEntity.getAge(),
                userEntity.getHairColor());

        Set<UUID> friends = userEntity
                .getFriends()
                .stream()
                .map(UserJpaEntity::getId)
                .collect(Collectors.toSet());

        user.setFriends(friends);

        return user;
    }
}