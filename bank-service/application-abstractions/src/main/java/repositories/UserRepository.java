package repositories;

import users.Gender;
import users.User;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface UserRepository {
    User save(User user);

    Optional<User> findById(UUID id);

    void delete(UUID id);

    List<User> findFriendsById(UUID id);

    List<User> findByHairColorAndGender(String hairColor, Gender gender);
}
