package services;

import authentifications.Authentication;
import lombok.RequiredArgsConstructor;
import org.jspecify.annotations.NullMarked;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;
import repositories.AuthentificationRepository;
import services.exceptions.NotAuthenticatedException;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AuthenticationService implements UserDetailsService {
    private final AuthentificationRepository repository;


    @Override
    @NullMarked
    public UserDetails loadUserByUsername(String username) throws NotAuthenticatedException {
        Authentication authentication = repository
                .findByLogin(username)
                .orElseThrow(() -> new NotAuthenticatedException("No user with this login"));

        return new User(
                authentication.login(),
                authentication.passwordHash(),
                List.of(new SimpleGrantedAuthority("ROLE_" + authentication.role())));
    }
}
