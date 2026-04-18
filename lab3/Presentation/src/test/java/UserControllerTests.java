import Runners.App;
import Services.UserService;
import Users.Gender;
import Users.UserDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.skyscreamer.jsonassert.JSONAssert;
import org.skyscreamer.jsonassert.JSONCompareMode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.*;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.util.HashSet;
import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(
        classes = App.class,
        properties = {
                "spring.autoconfigure.exclude=org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration",
                "spring.autoconfigure.exclude=org.springframework.boot.autoconfigure.orm.jpa.HibernateJpaAutoConfiguration"
        }
)
public class UserControllerTests {
    @Autowired
    private WebApplicationContext webApplicationContext;

    private MockMvc mockMvc;

    @MockitoBean
    private UserService userService;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders
                .webAppContextSetup(webApplicationContext)
                .build();
    }

    @Test
    public void testCreateUser() throws Exception {
        UserDto testUser = new UserDto(UUID.randomUUID(),
                "test name",
                23,
                Gender.MALE,
                "test color",
                new HashSet<>());

        Mockito.when(userService.createUser(any())).thenReturn(testUser);

        String requestJson = """
                    {
                        "name": "test name",
                        "age": 23,
                        "gender": "MALE",
                        "hairColor": "test color"
                    }
                """;

        String resultJson = mockMvc
                .perform(post("/api/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestJson))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString();

        JSONAssert.assertEquals("""
                    {
                        "name": "test name",
                        "age": 23,
                        "gender": "MALE",
                        "hairColor": "test color"
                    }
                """, resultJson, JSONCompareMode.LENIENT);
    }

    @Test
    public void testGetUserDetails() throws Exception {
        UUID userId = UUID.randomUUID();
        UserDto testUser = new UserDto(UUID.randomUUID(),
                "test name",
                23,
                Gender.MALE,
                "test color",
                new HashSet<>());

        Mockito.when(userService.getUserDetails(any())).thenReturn(testUser);

        String resultJson = mockMvc
                .perform(get("/api/users/{userId}", userId))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        JSONAssert.assertEquals("""
                    {
                        "name": "test name",
                        "age": 23,
                        "gender": "MALE",
                        "hairColor": "test color"
                    }
                """, resultJson, JSONCompareMode.LENIENT);
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