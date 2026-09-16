package services;

import accounts.requests.FindFriendsRequest;
import authentifications.Authentication;
import authentifications.Role;
import mapping.UserMapping;
import repositories.AuthentificationRepository;
import repositories.UserRepository;
import services.exceptions.NotFoundException;
import services.exceptions.OtherDataException;
import services.exceptions.UnauthorizedException;
import users.requests.*;
import users.User;
import users.UserDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@org.springframework.transaction.annotation.Transactional(readOnly = true)
@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final AuthentificationRepository authentificationRepository;

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

    public UserDto getUserDetails(GetUserDetailsRequest request,
                                  org.springframework.security.core.userdetails.User userDetails)
            throws NotFoundException,
            UnauthorizedException {
        Authentication authentication = authentificationRepository
                .findByLogin(userDetails.getUsername())
                .orElseThrow(() -> new UnauthorizedException("Incorrect login"));

        if ((authentication.role() == Role.CLIENT)
                && !(request.userId().equals(authentication.userId()))) {
            throw new OtherDataException("Try to read other data!");
        }

        User user = userRepository
                .findById(request.userId())
                .orElseThrow(() -> new NotFoundException("User not found"));

        return UserMapping.mapToDto(user);
    }


    @Transactional
    public void addFriend(AddFriendRequest request,
                          org.springframework.security.core.userdetails.User userDetails)
            throws NotFoundException,
            UnauthorizedException {
        Authentication authentication = authentificationRepository
                .findByLogin(userDetails.getUsername())
                .orElseThrow(() -> new UnauthorizedException("Incorrect login"));

        User user1 = userRepository
                .findById(authentication.userId())
                .orElseThrow(() -> new NotFoundException("User1 not found"));

        User user2 = userRepository
                .findById(request.user2())
                .orElseThrow(() -> new NotFoundException("User2 not found"));

        if (user1.getId().equals(user2.getId())) throw new IllegalArgumentException("Cannot add yourself as a friend");
        user1.getFriends().add(user2.getId());
        user2.getFriends().add(user1.getId());

        userRepository.save(user1);
        userRepository.save(user2);
    }

    @Transactional
    public void deleteFriend(DeleteFriendRequest request,
                             org.springframework.security.core.userdetails.User userDetails)
            throws NotFoundException,
            UnauthorizedException {
        Authentication authentication = authentificationRepository
                .findByLogin(userDetails.getUsername())
                .orElseThrow(() -> new UnauthorizedException("Incorrect login"));

        User user = userRepository
                .findById(authentication.userId())
                .orElseThrow(() -> new NotFoundException("User not found"));

        User friend = userRepository
                .findById(request.friendId())
                .orElseThrow(() -> new NotFoundException("User not found"));

        user.getFriends().remove(request.friendId());
        friend.getFriends().remove(user.getId());

        userRepository.save(user);
        userRepository.save(friend);
    }

    public List<UserDto> findFriends(FindFriendsRequest request, org.springframework.security.core.userdetails.User principal)
            throws NotFoundException {

        Authentication authentication = authentificationRepository.findByLogin(principal.getUsername())
                .orElseThrow(() -> new UnauthorizedException("Incorrect login"));
        if (authentication.role() == Role.CLIENT && !request.id().equals(authentication.userId())) {
            throw new OtherDataException("Try to read other data!");
        }
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