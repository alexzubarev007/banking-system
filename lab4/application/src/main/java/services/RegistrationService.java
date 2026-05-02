package services;

import authentifications.Authentication;
import authentifications.Role;
import lombok.RequiredArgsConstructor;
import mapping.UserMapping;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import registrations.RegisterAdminRequest;
import registrations.RegisterClientRequest;
import repositories.AuthentificationRepository;
import repositories.UserRepository;
import users.User;
import users.UserDto;

@Service
@RequiredArgsConstructor
@Transactional
public class RegistrationService {
    private final AuthentificationRepository authentificationRepository;
    private final UserRepository userRepository;

    public UserDto RegisterClient(RegisterClientRequest request) {
        User user = new User(null,
                request.name(),
                request.gender(),
                request.age(),
                request.hairColor());

        User addedUser = userRepository.save(user);

        System.out.println("User ID THERE!!!");
        System.out.println(addedUser.getId());

        Authentication client = new Authentication(
                null,
                request.login(),
                request.password(),
                Role.CLIENT,
                addedUser.getId());

        authentificationRepository.save(client);

        return UserMapping.mapToDto(addedUser);
    }

    public void RegisterAdmin(RegisterAdminRequest request) {
        Authentication admin = new Authentication(
                null,
                request.login(),
                request.password(),
                Role.ADMIN,
                null);

        authentificationRepository.save(admin);
    }
}
