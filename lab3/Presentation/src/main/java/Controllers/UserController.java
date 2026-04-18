package Controllers;

import Accounts.Requests.FindFriendsRequest;
import Services.UserService;
import Users.Gender;
import Users.Requests.AddFriendRequest;
import Users.Requests.CreateUserRequest;
import Users.Requests.FindByHairColorAndGenderRequest;
import Users.Requests.GetUserDetailsRequest;
import Users.UserDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Size;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
@Validated
public class UserController {
    private final UserService userService;

    @PostMapping
    @Operation(summary = "Create user operation")
    @ResponseStatus(HttpStatus.CREATED)
    @ApiResponse(responseCode = "201", description = "User created")
    @ApiResponse(responseCode = "400", description = "Incorrect input")
    public UserDto createUser(@Valid @RequestBody CreateUserRequest request) {
        return userService.createUser(request);
    }

    @GetMapping("/{userId}")
    @Operation(summary = "Get user details operation")
    @ApiResponse(responseCode = "200", description = "User details got")
    @ApiResponse(responseCode = "404", description = "User not found")
    public UserDto getUserDetails(@PathVariable UUID userId) {
        GetUserDetailsRequest request = new GetUserDetailsRequest(userId);
        return userService.getUserDetails(request);
    }

    @PatchMapping("/friends")
    @Operation(summary = "Add friends operation")
    @ApiResponse(responseCode = "200", description = "Friends added")
    @ApiResponse(responseCode = "404", description = "User not found")
    public void addFriend(@RequestBody AddFriendRequest request) {
        userService.addFriend(request);
    }

    @GetMapping("/friends/{id}")
    @Operation(summary = "Find friends operation")
    @ApiResponse(responseCode = "200", description = "Friends found")
    @ApiResponse(responseCode = "404", description = "User not found")
    public List<UserDto> findFriends(@PathVariable UUID id) {
        FindFriendsRequest request = new FindFriendsRequest(id);
        return userService.findFriends(request);
    }

    @GetMapping("/filters")
    @Operation(summary = "Find by hair color and gender operation")
    @ApiResponse(responseCode = "200", description = "Users found")
    @ApiResponse(responseCode = "400", description = "Incorrect name")
    public List<UserDto> findByHairColorAndGender(
            @Size(min = 2, max = 30, message = "Hair color must be between 2 and 30 characters")
            @RequestParam(required = false) String hairColor,
            @RequestParam(required = false) Gender gender) {
        FindByHairColorAndGenderRequest request = new FindByHairColorAndGenderRequest(hairColor, gender);
        return userService.findByHairColorAndGender(request);
    }
}