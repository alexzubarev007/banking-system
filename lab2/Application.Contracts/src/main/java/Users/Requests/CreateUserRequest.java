package Users.Requests;

import Users.Gender;

public record CreateUserRequest(String name,
                                int age,
                                Gender gender,
                                String hairColor) { }
