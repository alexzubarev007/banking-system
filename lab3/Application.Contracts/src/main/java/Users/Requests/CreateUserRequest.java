package Users.Requests;

import Users.Gender;
import io.swagger.v3.oas.annotations.media.Schema;

public record CreateUserRequest(
        @Schema(description = "User name")
        String name,
        @Schema(description = "User age")
        int age,
        @Schema(description = "User gender")
        Gender gender,
        @Schema(description = "User hair color")
        String hairColor) {
}
