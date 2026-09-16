package runners;

import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import repositories.AuthentificationRepository;
import registrations.RegisterAdminRequest;
import services.RegistrationService;

@Component
public class AdminBootstrap implements ApplicationRunner {
    private final RegistrationService registration;
    private final AuthentificationRepository accounts;
    private final String login;
    private final String password;

    public AdminBootstrap(
            RegistrationService registration,
            AuthentificationRepository accounts,
            @Value("${bootstrap.admin-login:}") String login,
            @Value("${bootstrap.admin-password:}") String password) {
        this.registration = registration;
        this.accounts = accounts;
        this.login = login;
        this.password = password;
    }

    @Override
    public void run(ApplicationArguments args) {
        if (login.isBlank()) return;
        if (accounts.findByLogin(login).isPresent()) return;
        if (password.length() < 12)
            throw new IllegalArgumentException(
                    "Bootstrap password must contain at least 12 characters");
        registration.registerAdmin(new RegisterAdminRequest(login, password));
    }
}
