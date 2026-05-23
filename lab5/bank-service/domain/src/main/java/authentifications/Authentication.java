package authentifications;

import java.util.UUID;

public record Authentication(UUID id,
                             String login,
                             String passwordHash,
                             Role role,
                             UUID userId) {
}
