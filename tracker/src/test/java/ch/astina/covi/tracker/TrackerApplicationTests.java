package ch.astina.covi.tracker;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class TrackerApplicationTests
{
    @Autowired
    private MockMvc mockMvc;

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
    }

    @Test
    void saveSubmissionByForm() throws Exception
    {
        mockMvc.perform(post("/form")
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
                .andExpect(header().string("Location", "https://www.covidtracker.ch/response.html"));
    }

    @Test
    public void testErrorHandling() throws Exception
    {
        mockMvc.perform(post("/form"))
                .andExpect(status().isFound())
                .andExpect(header().string("Location", "https://www.covidtracker.ch/?error=true"));
    }
}
