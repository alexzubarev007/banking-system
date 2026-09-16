package controllers;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import registrations.RegisterAdminRequest;
import registrations.RegisterClientRequest;
import services.RegistrationService;
import users.UserDto;

@RestController
@RequestMapping("/api/registration")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
@Validated
public class RegistrationController {
    private final RegistrationService registrationService;

    @PostMapping("/client")
    @Operation(summary = "Register client operation")
    @ApiResponse(responseCode = "200", description = "Registered")
    @ApiResponse(responseCode = "403", description = "Forbidden")
    public UserDto registerClient(@Valid @RequestBody RegisterClientRequest request) {
        return registrationService.registerClient(request);
    }

    @PostMapping("/admin")
    @Operation(summary = "Register admin operation")
    @ApiResponse(responseCode = "200", description = "Registered")
    @ApiResponse(responseCode = "403", description = "Forbidden")
    public void registerAdmin(@Valid @RequestBody RegisterAdminRequest request) {
        registrationService.registerAdmin(request);
    }
}
