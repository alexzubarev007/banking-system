package Users;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.Set;
import java.util.UUID;

public record UserDto(
        @Schema(description = "User id")
        UUID id,
        @Schema(description = "User name")
        String name,
        @Schema(description = "User age")
        int age,
        @Schema(description = "User gender")
        Gender gender,
        @Schema(description = "User hair color")
        String hairColor,
        @Schema(description = "User friends")
        Set<UUID> friends) {
}
