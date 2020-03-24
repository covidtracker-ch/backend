package ch.astina.covi.tracker;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;
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
                        "\t\"yearOfBirth\": 1960,\n" +
                        "\t\"zip\": \"8708\",\n" +
                        "\t\"phoneDigits\": \"9404\",\n" +
                        "\t\"feelsHealthy\": 1,\n" +
                        "\t\"hasBeenTested\": true,\n" +
                        "\t\"whereTested\": \"Kantonsspital Zürich\",\n" +
                        "\t\"whenTested\": \"2020-03-23\",\n" +
                        "\t\"worksInHealth\": \"no\",\n" +
                        "\t\"wasAbroad\": \"italy\",\n" +
                        "\t\"wasInContactWithCase\": true,\n" +
                        "\t\"dateContacted\": \"2020-03-10\",\n" +
                        "\t\"chronicConditionType\": \"no\",\n" +
                        "\t\"feverSince\": null,\n" +
                        "\t\"coughingSince\": 3,\n" +
                        "\t\"dyspneaSince\": null,\n" +
                        "\t\"tirednessSince\": null,\n" +
                        "\t\"throatSince\": null\n" +
                        "}"))
                .andExpect(status().isAccepted());

        db.query("select * from covid_submission order by _created desc limit 1", rs -> {

            assertEquals("female", rs.getString("sex"));
            assertEquals(1960, rs.getInt("year_of_birth"));
            assertEquals(LocalDate.of(2020, 3, 23), rs.getObject("when_tested", LocalDate.class));
            assertNull(rs.getObject("symptom_fever", Integer.class));
            assertEquals(3, rs.getInt("symptom_coughing"));
            assertNotNull(rs.getDate("_created"));
        });
    }
}
