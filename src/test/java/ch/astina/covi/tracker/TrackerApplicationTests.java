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
    	db.execute("create table COVID_SUBMISSION\n" +
				"      (\n" +
				"      \tid bigint auto_increment,\n" +
				"      \tage int null,\n" +
				"      \tcreated datetime default current_timestamp() null,\n" +
				"      \tconstraint COVID_SUBMISSION_pk\n" +
				"      \t\tprimary key (id)\n" +
				"      )");

        mockMvc.perform(post("/save")
				.contentType(MediaType.APPLICATION_JSON)
				.content("" +
						"{\n" +
						"\t\"isMale\": true,\n" +
						"\t\"age\": 25,\n" +
						"\t\"zip\": 8004,\n" +
						"\t\"hasBeenTested\": false,\n" +
						"\t\"dateTested\": \"\",\n" +
						"\t\"worksInHealth\": false,\n" +
						"\t\"travelled\": false,\n" +
						"\t\"chronicalDisease\": false,\n" +
						"\t\"fever\": false,\n" +
						"\t\"coughing\": false,\n" +
						"\t\"dyspnea\": false,\n" +
						"\t\"phoneDigits\": \"0009\"\n" +
						"}"))
                .andExpect(status().isAccepted());
    }
}
