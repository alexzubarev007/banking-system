package Repositories;

import Users.User;

import java.util.Optional;
import java.util.UUID;

public interface UserRepository {
    User add(User user);

    Optional<User> findById(UUID id);

    User update(User user);

    void delete(UUID id);
}
