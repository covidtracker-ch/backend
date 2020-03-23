package ch.astina.covi.tracker;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class TrackerApplicationTests
{
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private JdbcTemplate db;

    @Test
    void contextLoads()
    {
    }

    @Test
    void saveSubmission() throws Exception
    {
        mockMvc.perform(post("/save")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\n" +
                        "\t\"sex\": \"female\",\n" +
                        "\t\"age\": 26,\n" +
                        "\t\"zip\": \"8708\",\n" +
                        "\t\"phoneDigits\": \"9404\",\n" +
                        "\t\"feelsHealthy\": false,\n" +
                        "\t\"hasBeenTested\": true,\n" +
                        "\t\"whereTested\": \"Kantonsspital Zürich\",\n" +
                        "\t\"whenTested\": \"2020-03-23\",\n" +
                        "\t\"worksInHealth\": \"no\",\n" +
                        "\t\"wasAbroad\": \"italy\",\n" +
                        "\t\"wasInContactWithCase\": \"2020-03-10\",\n" +
                        "\t\"chronicCondition\": \"none\",\n" +
                        "\t\"fever\": null,\n" +
                        "\t\"coughing\": 3,\n" +
                        "\t\"dyspnea\": null,\n" +
                        "\t\"tiredness\": null,\n" +
                        "\t\"throat\": null\n" +
                        "}"))
                .andExpect(status().isAccepted());
    }
}
