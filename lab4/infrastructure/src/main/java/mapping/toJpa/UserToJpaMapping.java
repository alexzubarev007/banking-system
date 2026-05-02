package mapping.toJpa;

import entities.UserJpaEntity;
import repositories.jpa.JpaUserRepository;
import users.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Set;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class UserToJpaMapping {
    private final JpaUserRepository jpaUserRepository;

    public UserJpaEntity mapToJpa(User domainUser) {
        Set<UserJpaEntity> friends = domainUser
                .getFriends()
                .stream()
                .map(jpaUserRepository::getReferenceById)
                .collect(Collectors.toSet());


        return new UserJpaEntity(
                domainUser.getId(),
                domainUser.getName(),
                domainUser.getGender(),
                friends,
                domainUser.getAge(),
                domainUser.getHairColor());
    }
}