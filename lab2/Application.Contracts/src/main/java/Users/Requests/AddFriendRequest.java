package Users.Requests;

import java.util.UUID;

public record AddFriendRequest(UUID user1,
                               UUID user2) { }
