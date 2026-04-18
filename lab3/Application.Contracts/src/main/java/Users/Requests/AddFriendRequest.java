package Users.Requests;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.UUID;

public record AddFriendRequest(
        @Schema(description = "Friend1 id")
        UUID user1,
        @Schema(description = "Friend2 id")
        UUID user2) { }
