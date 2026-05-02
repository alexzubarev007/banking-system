package controllers;

import accounts.requests.FindFriendsRequest;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.User;
import services.UserService;
import users.Gender;
import users.requests.*;
import users.UserDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.RequiredArgsConstructor;
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

    @GetMapping("/{userId}")
    @Operation(summary = "Get user details operation")
    @ApiResponse(responseCode = "200", description = "User details got")
    @ApiResponse(responseCode = "403", description = "Forbidden")
    @ApiResponse(responseCode = "404", description = "User not found")
    @PreAuthorize("hasAnyRole('ADMIN', 'CLIENT')")
    public UserDto getUserDetails(@NotNull(message = "User id is required")
                                  @PathVariable UUID userId,
                                  @AuthenticationPrincipal User user) {
        GetUserDetailsRequest request = new GetUserDetailsRequest(userId);
        return userService.getUserDetails(request, user);
    }

    @PatchMapping("/friends/add")
    @Operation(summary = "Add friends operation")
    @ApiResponse(responseCode = "200", description = "Friends added")
    @ApiResponse(responseCode = "403", description = "Forbidden")
    @ApiResponse(responseCode = "404", description = "User not found")
    @PreAuthorize("hasRole('CLIENT')")
    public void addFriend(@Valid @RequestBody AddFriendRequest request,
                          @AuthenticationPrincipal User user) {
        userService.addFriend(request, user);
    }

    @PatchMapping("/friends/delete")
    @Operation(summary = "Delete friends operation")
    @ApiResponse(responseCode = "200", description = "Friends added")
    @ApiResponse(responseCode = "403", description = "Forbidden")
    @ApiResponse(responseCode = "404", description = "User not found")
    @PreAuthorize("hasRole('CLIENT')")
    public void deleteFriend(@Valid @RequestBody DeleteFriendRequest request,
                             @AuthenticationPrincipal User user) {
        userService.deleteFriend(request, user);
    }

    @GetMapping("/friends/{id}")
    @Operation(summary = "Find friends operation")
    @ApiResponse(responseCode = "200", description = "Friends found")
    @ApiResponse(responseCode = "403", description = "Forbidden")
    @ApiResponse(responseCode = "404", description = "User not found")
    @PreAuthorize("hasAnyRole('ADMIN', 'CLIENT')")
    public List<UserDto> findFriends(@Valid @PathVariable UUID id) {
        FindFriendsRequest request = new FindFriendsRequest(id);
        return userService.findFriends(request);
    }

    @GetMapping("/filters")
    @Operation(summary = "Find by hair color and gender operation")
    @ApiResponse(responseCode = "200", description = "Users found")
    @ApiResponse(responseCode = "400", description = "Incorrect name")
    @ApiResponse(responseCode = "403", description = "Forbidden")
    @PreAuthorize("hasRole('ADMIN')")
    public List<UserDto> findByHairColorAndGender(
            @Size(min = 2, max = 30, message = "Hair color must be between 2 and 30 characters")
            @RequestParam(required = false) String hairColor,
            @RequestParam(required = false) Gender gender) {
        FindByHairColorAndGenderRequest request = new FindByHairColorAndGenderRequest(hairColor, gender);
        return userService.findByHairColorAndGender(request);
    }
}