package Services;

import Mapping.UserMapping;
import Repositories.UserRepository;
import Services.Exceptions.NotFoundException;
import TransactionManager.TransactionManager;
import Users.Requests.AddFriendRequest;
import Users.Requests.CreateUserRequest;
import Users.Requests.GetUserDetailsRequest;
import Users.User;
import Users.UserDto;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public class UserService {
    private TransactionManager transactionManager;
    private UserRepository userRepository;

    public UserDto createUser(CreateUserRequest request) {
        transactionManager.begin();
        try {
            User user = new User(null,
                    request.name(),
                    request.gender(),
                    request.age(),
                    request.hairColor());

            User addedUser = userRepository.add(user);

            transactionManager.commit();

            return UserMapping.mapToDto(addedUser);
        } catch (Exception exception) {
            transactionManager.rollback();
            throw exception;
        }
    }

    public UserDto getUserDetails(GetUserDetailsRequest request)
            throws NotFoundException {
        User user = userRepository
                .findById(request.userId())
                .orElseThrow(() -> new NotFoundException("User not found"));

        return UserMapping.mapToDto(user);
    }

    public void addFriend(AddFriendRequest request)
            throws NotFoundException {
        transactionManager.begin();
        try {
            User user1 = userRepository
                    .findById(request.user1())
                    .orElseThrow(() -> new NotFoundException("User1 not found"));

            User user2 = userRepository
                    .findById(request.user2())
                    .orElseThrow(() -> new NotFoundException("User2 not found"));

            user1.getFriends().add(user2.getId());
            user2.getFriends().add(user1.getId());

            userRepository.update(user1);
            userRepository.update(user2);

            transactionManager.commit();
        } catch(Exception exception) {
            transactionManager.rollback();
            throw exception;
        }
    }
}