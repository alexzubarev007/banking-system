package Users;

import lombok.Getter;
import lombok.Setter;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Getter
public class User {

    private final UUID id;
    private final String name;
    private final Gender gender;

    @Setter
    private Set<UUID> friends = new HashSet<>();

    @Setter
    private int age;

    @Setter
    private String hairColor;

    public User(UUID id,
                String name,
                Gender gender,
                int age,
                String hairColor) {
        this.id = id;
        this.name = name;
        this.gender = gender;
        this.age = age;
        this.hairColor = hairColor;
    }

    public void addFriend(UUID friendId) {
        friends.add(friendId);
    }
}