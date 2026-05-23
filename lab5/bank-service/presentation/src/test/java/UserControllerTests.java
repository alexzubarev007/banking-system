import controllers.UserController;
import services.UserService;
import users.Gender;
import users.UserDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;


import java.util.HashSet;
import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


public class UserControllerTests {

    private MockMvc mockMvc;

    @Mock
    private UserService userService;

    private UserController userController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        userController = new UserController(userService);
        mockMvc = MockMvcBuilders.standaloneSetup(userController).build();
    }

    @Test
    public void testFindFriends() throws Exception {
        UUID userId = UUID.randomUUID();

        UserDto testFriend1 = new UserDto(
                UUID.randomUUID(),
                "test friend1",
                20,
                Gender.MALE,
                "test color",
                new HashSet<>()
        );

        UserDto testFriend2 = new UserDto(
                UUID.randomUUID(),
                "test friend2",
                22,
                Gender.FEMALE,
                "test color",
                new HashSet<>()
        );

        List<UserDto> friends = List.of(testFriend1, testFriend2);

        Mockito.when(userService.findFriends(any()))
                .thenReturn(friends);

        mockMvc.perform(get("/api/users/friends/{id}", userId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name").value("test friend1"))
                .andExpect(jsonPath("$[1].name").value("test friend2"));
    }

    @Test
    public void testFindByHairColorAndGender() throws Exception {

        UserDto testUser = new UserDto(
                UUID.randomUUID(),
                "test name",
                23,
                Gender.MALE,
                "test color",
                new HashSet<>()
        );

        List<UserDto> users = List.of(testUser);

        Mockito.when(userService.findByHairColorAndGender(any()))
                .thenReturn(users);

        mockMvc.perform(get("/api/users/filters")
                        .param("hairColor", "test color")
                        .param("gender", "MALE"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name").value("test name"))
                .andExpect(jsonPath("$[0].hairColor").value("test color"))
                .andExpect(jsonPath("$[0].gender").value("MALE"));
    }
}