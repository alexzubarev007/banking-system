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
    private final org.springframework.security.crypto.password.PasswordEncoder passwordEncoder;

    public UserDto registerClient(RegisterClientRequest request) {
        User user = new User(null,
                request.name(),
                request.gender(),
                request.age(),
                request.hairColor());

        User addedUser = userRepository.save(user);


        Authentication client = new Authentication(
                null,
                request.login(),
                passwordEncoder.encode(request.password()),
                Role.CLIENT,
                addedUser.getId());

        authentificationRepository.save(client);

        return UserMapping.mapToDto(addedUser);
    }

    public void registerAdmin(RegisterAdminRequest request) {
        Authentication admin = new Authentication(
                null,
                request.login(),
                passwordEncoder.encode(request.password()),
                Role.ADMIN,
                null);

        authentificationRepository.save(admin);
    }
}
