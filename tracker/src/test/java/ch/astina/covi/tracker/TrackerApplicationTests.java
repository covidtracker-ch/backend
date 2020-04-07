package ch.astina.covi.tracker;

import ch.astina.covi.test.PostgresTestContext;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@PostgresTestContext
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
                        "}")
                .header("user-agent", "test"))
                .andExpect(status().isAccepted());

        db.query("select * from covid_submission order by _created desc limit 1", rs -> {

            assertEquals("female", rs.getString("sex"));
            assertEquals(1960, rs.getInt("year_of_birth"));
            assertEquals(LocalDate.of(2020, 3, 23), rs.getObject("when_tested", LocalDate.class));
            assertNull(rs.getObject("symptom_fever", Integer.class));
            assertEquals(3, rs.getInt("symptom_coughing"));
            assertNotNull(rs.getDate("_created"));
            assertEquals("127.0.0.0", rs.getString("_ip_addr"));
        });
    }

    @Test
    void saveSubmissionByForm() throws Exception
    {
        MvcResult result = mockMvc.perform(post("/form")
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .param("sex", "male")
                .param("yearOfBirth", "1990")
                .param("zip", "9000")
                .param("phoneDigits", "0056")
                .param("feelsHealthy", "0")
                .param("hasBeenTested", "0")
                .param("testResult", "negative")
                .param("worksInHealth", "private_practice")
                .param("wasAbroad", "no")
                .param("wasInContactWithCase", "0")
                .param("chronicConditionType", "diabetes")
                .header("user-agent", "test"))
                .andExpect(status().isFound())
                .andReturn();
                // .andExpect(header().string("Location", startsWith("https://www.covidtracker.ch/response.html")));

        // inspect the Location header to match it against a regex
        assertTrue(
            result.getResponse().getHeader("Location").matches(
                "^https://www\\.covidtracker\\.ch/response\\.html\\?code=[0-9A-Z]{6}$"
            )
        );

        db.query("select * from covid_submission order by _created desc limit 1", rs -> {

            assertEquals("male", rs.getString("sex"));
            assertEquals(1990, rs.getInt("year_of_birth"));
            assertEquals("negative", rs.getString("test_result"));
            assertNull(rs.getObject("symptom_fever", Integer.class));
            assertNotNull(rs.getDate("_created"));
            assertEquals("127.0.0.0", rs.getString("_ip_addr"));
            assertEquals("08bd7b3f7d005739ab6b53fe71548ab5d65ccfca5651e1163e228dd264f3c10a", rs.getString("_ip_hash"));
            assertEquals("88cd2108b5347d973cf39cdf9053d7dd42704876d8c9a9bd8e2d168259d3ddf7", rs.getString("_ua_hash"));
        });
    }


    @Test
    void saveWithoutCodeGetsNewOne() throws Exception
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
                .andExpect(status().is(202));

        db.query("select * from covid_submission order by _created desc limit 1", rs -> {
            System.out.println("Code: " + rs.getString("participant_code"));

            assertTrue(rs.getString("participant_code").matches("^[0-9A-Z]{6}$"));
            assertEquals("female", rs.getString("sex"));
            assertEquals(1960, rs.getInt("year_of_birth"));
            assertEquals(LocalDate.of(2020, 3, 23), rs.getObject("when_tested", LocalDate.class));
            assertNull(rs.getObject("symptom_fever", Integer.class));
            assertEquals(3, rs.getInt("symptom_coughing"));
            assertNotNull(rs.getDate("_created"));
            assertEquals("127.0.0.0", rs.getString("_ip_addr"));
        });
    }

    @Test
    void saveWithCodeGetsSameOne() throws Exception
    {
        final String code = "A7CXWG";

        mockMvc.perform(post("/save")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\n" +
                        "\t\"sex\": \"female\",\n" +
                        "\t\"participantCode\": \"" + code + "\",\n" +
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
                .andExpect(status().is(202));

        db.query("select * from covid_submission order by _created desc limit 1", rs -> {
            assertEquals(code, rs.getString("participant_code"));
            assertEquals("female", rs.getString("sex"));
            assertEquals(1960, rs.getInt("year_of_birth"));
            assertEquals(LocalDate.of(2020, 3, 23), rs.getObject("when_tested", LocalDate.class));
            assertNull(rs.getObject("symptom_fever", Integer.class));
            assertEquals(3, rs.getInt("symptom_coughing"));
            assertNotNull(rs.getDate("_created"));
            assertEquals("127.0.0.0", rs.getString("_ip_addr"));
        });
    }

    @Test
    public void testErrorHandling() throws Exception
    {
        mockMvc.perform(post("/form"))
                .andExpect(status().isFound())
                .andExpect(header().string("Location", "https://www.covidtracker.ch/?error=true"));
    }
}
