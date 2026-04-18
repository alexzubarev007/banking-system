package Services;

import Accounts.Requests.FindFriendsRequest;
import Mapping.UserMapping;
import Repositories.UserRepository;
import Services.Exceptions.NotFoundException;
import Users.Requests.AddFriendRequest;
import Users.Requests.CreateUserRequest;
import Users.Requests.FindByHairColorAndGenderRequest;
import Users.Requests.GetUserDetailsRequest;
import Users.User;
import Users.UserDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;

    @Transactional
    public UserDto createUser(CreateUserRequest request) {
        User user = new User(null,
                request.name(),
                request.gender(),
                request.age(),
                request.hairColor());

        User addedUser = userRepository.save(user);

        return UserMapping.mapToDto(addedUser);
    }

    public UserDto getUserDetails(GetUserDetailsRequest request)
            throws NotFoundException {
        User user = userRepository
                .findById(request.userId())
                .orElseThrow(() -> new NotFoundException("User not found"));

        return UserMapping.mapToDto(user);
    }


    @Transactional
    public void addFriend(AddFriendRequest request)
            throws NotFoundException {
        User user1 = userRepository
                .findById(request.user1())
                .orElseThrow(() -> new NotFoundException("User1 not found"));

        User user2 = userRepository
                .findById(request.user2())
                .orElseThrow(() -> new NotFoundException("User2 not found"));

        user1.getFriends().add(user2.getId());
        user2.getFriends().add(user1.getId());

        userRepository.save(user1);
        userRepository.save(user2);
    }

    public List<UserDto> findFriends(FindFriendsRequest request)
            throws NotFoundException {

        if (userRepository.findById(request.id()).isEmpty()) {
            throw new NotFoundException("User not found");
        }


        return userRepository
                .findFriendsById(request.id())
                .stream()
                .map(UserMapping::mapToDto)
                .toList();
    }

    public List<UserDto> findByHairColorAndGender(FindByHairColorAndGenderRequest request) {
        return userRepository.findByHairColorAndGender(
                        request.hairColor(),
                        request.gender())
                .stream()
                .map(UserMapping::mapToDto)
                .toList();
    }
}