package tennisboard;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import tennisboard.request.CreateMatchRequest;
import tennisboard.request.UpdateMatchRequest;
import tennisboard.response.CreateMatchResponse;
import tennisboard.service.logic.Match;
import tennisboard.service.logic.MatchScore;
import tennisboard.service.logic.Player;
import tennisboard.service.logic.Side;
import tennisboard.storage.OngoingMatchesStorage;

import java.util.UUID;

import static org.hamcrest.core.IsNull.nullValue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

//toDo MockMvcTester - перепиисать с ним (или пописать в 5/6 проекте с ним)
@ExtendWith(SpringExtension.class)
@WebAppConfiguration
@ContextConfiguration(classes = WebConfig.class)
public class MatchesIntegrationTest {

    @Autowired
    private WebApplicationContext applicationContext;

    @Autowired
    private OngoingMatchesStorage storage;

    private ObjectMapper objectMapper = new ObjectMapper();

    private MockMvc mockMvc;

    @BeforeEach
    public void setUp() {
        this.mockMvc = MockMvcBuilders.webAppContextSetup(applicationContext).build();
    }

    @Test
    public void createNewMatchBasic() throws Exception {
        CreateMatchRequest request = new CreateMatchRequest("Agassi", "Federerr");

        mockMvc.perform(post("/matches")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.id").isString());
    }

    @Test
    public void getCreatedNewMatchBasic() throws Exception {
        CreateMatchRequest request = new CreateMatchRequest("Agassi", "Federerr");

        MvcResult result = mockMvc.perform(post("/matches")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.id").isString())
                .andReturn();

        String content = result.getResponse().getContentAsString();

        CreateMatchResponse response = objectMapper.readValue(content, CreateMatchResponse.class);
        UUID id = response.id();

        mockMvc.perform(get("/matches/{uuid}", id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.firstPlayerName").value("agassi"))
                .andExpect(jsonPath("$.secondPlayerName").value("federerr"))
                .andExpect(jsonPath("$.firstPlayerPoints").value("0"))
                .andExpect(jsonPath("$.secondPlayerPoints").value("0"))
                .andExpect(jsonPath("$.firstPlayerGames").value(0))
                .andExpect(jsonPath("$.secondPlayerGames").value(0))
                .andExpect(jsonPath("$.firstPlayerSets").value(0))
                .andExpect(jsonPath("$.secondPlayerSets").value(0))
                .andExpect(jsonPath("$.winnerName").value(nullValue()));
    }

    @Test
    public void createNewMatchFailSinceNamesAreEmpty() throws Exception {
        CreateMatchRequest request = new CreateMatchRequest("", "");

        mockMvc.perform(post("/matches")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").exists());
    }

    @Test
    public void createNewMatchFailSinceSameNames() throws Exception {
        CreateMatchRequest request = new CreateMatchRequest("agassi", "Agassi");

        mockMvc.perform(post("/matches")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").exists())
                .andExpect(jsonPath("$.message").value("Names are the same!"));

    }

    @Test
    public void getCreatedNewMatchFailSinceUUIDIsNotFound() throws Exception {
        mockMvc.perform(get("/matches/{uuid}", "91f1b06b-aa1a-482d-95f2-cf52c968f3f0"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").exists());
    }

    @Test
    public void getCreatedNewMatchFailSinceUUIDIsWrong() throws Exception {
        mockMvc.perform(get("/matches/{uuid}", "1234-5678-abcd-efgh"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").exists());
    }

    @Test
    public void addPointToSideAWorks() throws Exception {
        CreateMatchRequest requestForCreate = new CreateMatchRequest("Agassi", "Federerr");

        MvcResult result = mockMvc.perform(post("/matches")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestForCreate)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.id").isString())
                .andReturn();

        String content = result.getResponse().getContentAsString();

        CreateMatchResponse response = objectMapper.readValue(content, CreateMatchResponse.class);
        UUID id = response.id();

        UpdateMatchRequest requestForUpdate = new UpdateMatchRequest("Agassi");

        mockMvc.perform(post("/matches/{uuid}/point", id)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(requestForUpdate)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.firstPlayerName").value("agassi"))
                .andExpect(jsonPath("$.secondPlayerName").value("federerr"))
                .andExpect(jsonPath("$.firstPlayerPoints").value("15"))
                .andExpect(jsonPath("$.secondPlayerPoints").value("0"))
                .andExpect(jsonPath("$.firstPlayerGames").value(0))
                .andExpect(jsonPath("$.secondPlayerGames").value(0))
                .andExpect(jsonPath("$.firstPlayerSets").value(0))
                .andExpect(jsonPath("$.secondPlayerSets").value(0))
                .andExpect(jsonPath("$.winnerName").value(nullValue()));
    }

    //будет работать нормально, когда будет БД. сейчаас - валится, т.к. он идет во времянку,
    //а к этому времени уже матч оттуда удалили
/*    @Test
    public void getCreatedNewMatchBasic111() throws Exception {
        String firstName = "Agassi";
        String secondName = "Federer";

        UUID id = UUID.randomUUID();
        Match match = new Match(
                id,
                new Player(null, firstName.toLowerCase()),
                new Player(null, secondName.toLowerCase()),
                new MatchScore()
        );
        for (int i = 0; i < 47; i++) {
            match.getMatchScore().increasePoint(Side.A);
        }

        storage.save(match);

        UpdateMatchRequest requestForUpdate = new UpdateMatchRequest("Agassi");

        mockMvc.perform(post("/matches/{uuid}/point", id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestForUpdate)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.firstPlayerName").value("agassi"))
                .andExpect(jsonPath("$.secondPlayerName").value("federerr"))
                .andExpect(jsonPath("$.firstPlayerPoints").value("0"))
                .andExpect(jsonPath("$.secondPlayerPoints").value("0"))
                .andExpect(jsonPath("$.firstPlayerGames").value(0))
                .andExpect(jsonPath("$.secondPlayerGames").value(0))
                .andExpect(jsonPath("$.firstPlayerSets").value(0))
                .andExpect(jsonPath("$.secondPlayerSets").value(0))
                .andExpect(jsonPath("$.winnerName").value("agassi"));
    }*/
}