package Users.Requests;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public record AddFriendRequest(
        @Schema(description = "Friend1 id")
        @NotNull(message = "Friend id is required")
        UUID user1,
        @Schema(description = "Friend2 id")
        @NotNull(message = "Friend id is required")
        UUID user2) { }
