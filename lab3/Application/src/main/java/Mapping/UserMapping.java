package Mapping;
import Users.User;
import Users.UserDto;

public class UserMapping {
    public static UserDto mapToDto(User user) {
        return new UserDto(
                user.getId(),
                user.getName(),
                user.getAge(),
                user.getGender(),
                user.getHairColor(),
                user.getFriends());
    }
}