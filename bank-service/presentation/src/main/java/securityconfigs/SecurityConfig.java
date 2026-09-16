package securityconfigs;

import authentifications.Role;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.CsrfConfigurer;
import org.springframework.security.config.annotation.web.configurers.FormLoginConfigurer;
import org.springframework.security.config.annotation.web.configurers.LogoutConfigurer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) {
        return http.csrf(CsrfConfigurer::disable)
                .authorizeHttpRequests(
                        auth ->
                                auth.requestMatchers("/api/**")
                                        .hasAnyRole(Role.ADMIN.name(), Role.CLIENT.name())
                                        .anyRequest()
                                        .authenticated())
                .exceptionHandling(
                        errors ->
                                errors.defaultAuthenticationEntryPointFor(
                                        new org.springframework.security.web.authentication
                                                .HttpStatusEntryPoint(
                                                org.springframework.http.HttpStatus.UNAUTHORIZED),
                                        request ->
                                                request.getRequestURI()
                                                        .startsWith(
                                                                request.getContextPath()
                                                                        + "/api/")))
                .formLogin(FormLoginConfigurer::permitAll)
                .logout(LogoutConfigurer::permitAll)
                .build();
    }

    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
