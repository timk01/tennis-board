package tennisboard;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.junit.jupiter.api.AfterEach;
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
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.support.TransactionTemplate;
import org.springframework.web.context.WebApplicationContext;
import tennisboard.entity.MatchEntity;
import tennisboard.entity.PlayerEntity;
import tennisboard.repository.MatchRepository;
import tennisboard.repository.PlayerRepository;
import tennisboard.request.CreateMatchRequest;
import tennisboard.request.UpdateMatchRequest;
import tennisboard.response.CreateMatchResponse;
import tennisboard.service.logic.Match;
import tennisboard.service.logic.MatchScore;
import tennisboard.service.logic.Player;
import tennisboard.service.logic.Side;
import tennisboard.storage.OngoingMatchesStorage;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.hamcrest.core.IsNull.nullValue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
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

    @Autowired
    private PlayerRepository playerRepository;

    @Autowired
    private MatchRepository matchRepository;

    @PersistenceContext
    EntityManager em;

    @Autowired
    private PlatformTransactionManager transactionManager;

    private ObjectMapper objectMapper = new ObjectMapper();
    private MockMvc mockMvc;
    private String testPrefix;

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
        CreateMatchRequest requestForCreate = new CreateMatchRequest("agassi", "federerr");

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

    @Test
    public void add48PointsToSideAProducesWinner() throws Exception {
        String firstName = "agassi";
        String secondName = "federerr";

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

        UpdateMatchRequest requestForUpdate = new UpdateMatchRequest("agassi");

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
                .andExpect(jsonPath("$.firstPlayerSets").value(2))
                .andExpect(jsonPath("$.secondPlayerSets").value(0))
                .andExpect(jsonPath("$.winnerName").value("agassi"));
    }


    /**
     * метод использует подготовленную "соль" для эмуляции уникальности данных
     * для тстирования реального поведения БД передает имена в метод создания игроков - но игроков создается 4
     * (в данный момент временные игроки с результатом матчей появляются в БД)
     * найденных же игр, где принимал участие игрок А = 2 из 4
     * <p>
     * после метода происходит подчистка БД
     *
     * @throws Exception
     */
    @Test
    public void gettingOneWinnerFromSeveralMatchesWorksFine() throws Exception {
        String firstPlayerName = testPrefix + "agassi";
        String secondPlayerName = testPrefix + "federerr";
        String thirdPlayerName = testPrefix + "nadal";
        String fourthPlayerName = testPrefix + "djochrovic";
        prepareMatchesWithDifferentWinners(
                firstPlayerName,
                secondPlayerName,
                thirdPlayerName,
                fourthPlayerName,
                1,
                1);

        int page = 1;

        mockMvc.perform(get("/matches")
                        .param("page", String.valueOf(page))
                        .param("player_name", firstPlayerName))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.matches.size()").value(2))
                .andExpect(jsonPath("$.matches[0].firstPlayerName").value(firstPlayerName))
                .andExpect(jsonPath("$.matches[0].secondPlayerName").value(secondPlayerName))
                .andExpect(jsonPath("$.matches[0].winnerName").value(firstPlayerName))
                .andExpect(jsonPath("$.matches[1].firstPlayerName").value(firstPlayerName))
                .andExpect(jsonPath("$.matches[1].secondPlayerName").value(secondPlayerName))
                .andExpect(jsonPath("$.currentPage").value(1))
                .andExpect(jsonPath("$.totalPages").value(1));
    }

    @Test
    public void gettingZeroPagesWithPlayerNameOutsideTable() throws Exception {
        String firstPlayerName = testPrefix + "agassi";
        String secondPlayerName = testPrefix + "federerr";
        String thirdPlayerName = testPrefix + "nadal";
        String fourthPlayerName = testPrefix + "djochrovic";
        prepareMatchesWithDifferentWinners(
                firstPlayerName,
                secondPlayerName,
                thirdPlayerName,
                fourthPlayerName,
                1,
                1);

        int page = 1;

        String playerNameOutsideTable = testPrefix + "sampras";

        mockMvc.perform(get("/matches")
                        .param("page", String.valueOf(page))
                        .param("player_name", playerNameOutsideTable))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.matches.size()").value(0))
                .andExpect(jsonPath("$.currentPage").value(1))
                .andExpect(jsonPath("$.totalPages").value(0));
    }

    private List<MatchEntity> prepareMatchesWithDifferentWinners(
            String firstName,
            String secondName,
            String thirdName,
            String fourthName,
            int firstWinnerTimes,
            int secondWinnerTimes) {
        PlayerEntity player1 = new PlayerEntity(firstName);
        PlayerEntity player2 = new PlayerEntity(secondName);
        PlayerEntity player3 = new PlayerEntity(thirdName);
        PlayerEntity player4 = new PlayerEntity(fourthName);

        player1 = playerRepository.save(player1);
        player2 = playerRepository.save(player2);
        player3 = playerRepository.save(player3);
        player4 = playerRepository.save(player4);

        List<MatchEntity> matchEntityList = new ArrayList<>();
        for (int i = 0; i < firstWinnerTimes; i++) {
            matchEntityList.add(matchRepository.save(new MatchEntity(player1, player2, player1)));
        }

        for (int i = 0; i < secondWinnerTimes; i++) {
            matchEntityList.add(matchRepository.save(new MatchEntity(player1, player2, player2)));
        }

        matchEntityList.add(matchRepository.save(new MatchEntity(player3, player4, player3)));
        matchEntityList.add(matchRepository.save(new MatchEntity(player4, player3, player3)));

        return matchEntityList;
    }

    /*
    компромисс по очистке БД от временных данных вместо заведения новой/засорения старой
     */

    void clearDbFromTempData(String pattern) {
        TransactionTemplate transactionTemplate = new TransactionTemplate(transactionManager);

        transactionTemplate.executeWithoutResult(
                (status) -> {
                    em.createQuery("""               
                                    delete from MatchEntity m
                                    where m.firstPlayer.name like :pattern
                                    or m.secondPlayer.name like :pattern
                                    or m.winner.name like :pattern
                                    """
                            ).setParameter("pattern", pattern + "%")
                            .executeUpdate();
                    em.createQuery("""               
                                    delete from PlayerEntity p
                                    where p.name like :pattern
                                    or p.name like :pattern
                                    or p.name like :pattern
                                    """
                            ).setParameter("pattern", pattern + "%")
                            .executeUpdate();
                });
    }

    @AfterEach
    void tearDown() {
        clearDbFromTempData(testPrefix);
    }

    @BeforeEach
    void startUp() {
        testPrefix = "test_" + UUID.randomUUID() + "_";
    }
}