package registrations;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import users.Gender;

public record RegisterClientRequest(
        @Schema(description = "Login")
        @Size(min = 2, max = 30, message = "Login must be between 2 and 30 characters")
        @jakarta.validation.constraints.NotBlank
        String login,
        @Schema(description = "Password")
        @Size(min = 2, max = 30, message = "Password must be between 2 and 30 characters")
        @jakarta.validation.constraints.NotBlank
        String password,
        @Schema(description = "User name")
        @Size(min = 2, max = 30, message = "Name must be between 2 and 30 characters")
        @jakarta.validation.constraints.NotBlank
        String name,
        @Schema(description = "User age")
        @Min(value = 0, message = "Age must be positive")
        @Max(value = 200, message = "Age must be less than 200")
        int age,

        @Schema(description = "User gender")
        @NotNull(message = "Gender is required")
        Gender gender,
        @Schema(description = "User hair color")
        @Size(min = 2, max = 30, message = "Name must be between 2 and 30 characters")
        @jakarta.validation.constraints.NotBlank
        String hairColor) { }
