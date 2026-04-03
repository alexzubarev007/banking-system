package Users;

import java.util.Set;
import java.util.UUID;

public record UserDto(UUID id,
                      String name,
                      int age,
                      Gender gender,
                      String hairColor,
                      Set<UUID> friends) { }
