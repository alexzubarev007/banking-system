package registrations;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Size;

public record RegisterAdminRequest(
        @Schema(description = "Login")
        @Size(min = 2, max = 30, message = "Login must be between 2 and 30 characters")
        String login,
        @Schema(description = "Password")
        @Size(min = 2, max = 30, message = "Password must be between 2 and 30 characters")
        String password
        ) { }
