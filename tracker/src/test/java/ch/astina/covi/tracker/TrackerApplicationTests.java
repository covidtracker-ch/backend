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

import static org.hamcrest.Matchers.startsWith;
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
                        "\t\"ageRange\": \"51-60\",\n" +
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
                        "\t\"feverSince\": null,\n" +
                        "\t\"coughingSince\": \"2020-03-10\",\n" +
                        "\t\"dyspneaSince\": null,\n" +
                        "\t\"tirednessSince\": null,\n" +
                        "\t\"throatSince\": null\n" +
                        "}")
                .header("user-agent", "test"))
                .andExpect(status().isAccepted());

        db.query("select * from covid_submission order by _created desc limit 1", rs -> {

            assertEquals("female", rs.getString("sex"));
            assertEquals("51-60", rs.getString("age_range"));
            assertEquals(LocalDate.of(2020, 3, 23), rs.getObject("when_tested", LocalDate.class));
            assertNull(rs.getObject("symptom_fever", LocalDate.class));
            assertEquals(LocalDate.of(2020, 3, 10), rs.getObject("symptom_coughing", LocalDate.class));
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
                .param("ageRange", "21-30")
                .param("zip", "9000")
                .param("householdSize", "3")
                .param("phoneDigits", "0056")
                .param("feelsHealthy", "0")
                .param("previouslyUnhealthy", "0")
                .param("hasBeenTested", "0")
                .param("testResult", "negative")
                .param("worksInHealth", "private_practice")
                .param("wasAbroad", "no")
                .param("wasInContactWithCase", "0")
                .param("behavior", "self_isolation")
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
            assertEquals("21-30", rs.getString("age_range"));
            assertEquals(3, rs.getInt("household_size"));
            assertFalse(rs.getBoolean("feels_healthy"));
            assertFalse(rs.getBoolean("previously_unhealthy"));
            assertEquals("negative", rs.getString("test_result"));
            assertEquals("self_isolation", rs.getString("behavior"));
            assertNull(rs.getObject("symptom_fever", LocalDate.class));
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
                        "\t\"ageRange\": \"51-60\",\n" +
                        "\t\"zip\": \"8708\",\n" +
                        "\t\"phoneDigits\": \"9404\",\n" +
                        "\t\"feelsHealthy\": 1,\n" +
                        "\t\"previouslyUnhealthy\": 0,\n" +
                        "\t\"hasBeenTested\": true,\n" +
                        "\t\"whereTested\": \"Kantonsspital Zürich\",\n" +
                        "\t\"whenTested\": \"2020-03-23\",\n" +
                        "\t\"worksInHealth\": \"no\",\n" +
                        "\t\"wasAbroad\": \"italy\",\n" +
                        "\t\"wasInContactWithCase\": true,\n" +
                        "\t\"dateContacted\": \"2020-03-10\",\n" +
                        "\t\"feverSince\": null,\n" +
                        "\t\"coughingSince\": \"2020-03-10\",\n" +
                        "\t\"dyspneaSince\": null,\n" +
                        "\t\"tirednessSince\": null,\n" +
                        "\t\"throatSince\": null\n" +
                        "}"))
                .andExpect(status().isAccepted());

        db.query("select * from covid_submission order by _created desc limit 1", rs -> {
            System.out.println("Code: " + rs.getString("participant_code"));

            assertTrue(rs.getString("participant_code").matches("^[0-9A-Z]{6}$"));
            assertEquals("female", rs.getString("sex"));
            assertEquals("51-60", rs.getString("age_range"));
            assertTrue(rs.getBoolean("feels_healthy"));
            assertFalse(rs.getBoolean("previously_unhealthy"));
            assertEquals(LocalDate.of(2020, 3, 23), rs.getObject("when_tested", LocalDate.class));
            assertNull(rs.getObject("symptom_fever", LocalDate.class));
            assertEquals(LocalDate.of(2020, 3, 10), rs.getObject("symptom_coughing", LocalDate.class));
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
                        "\t\"ageRange\": \"51-60\",\n" +
                        "\t\"zip\": \"8708\",\n" +
                        "\t\"phoneDigits\": \"9404\",\n" +
                        "\t\"feelsHealthy\": 1,\n" +
                        "\t\"previouslyUnhealthy\": 0,\n" +
                        "\t\"hasBeenTested\": true,\n" +
                        "\t\"whereTested\": \"Kantonsspital Zürich\",\n" +
                        "\t\"whenTested\": \"2020-03-23\",\n" +
                        "\t\"worksInHealth\": \"no\",\n" +
                        "\t\"wasAbroad\": \"italy\",\n" +
                        "\t\"wasInContactWithCase\": true,\n" +
                        "\t\"dateContacted\": \"2020-03-10\",\n" +
                        "\t\"feverSince\": null,\n" +
                        "\t\"coughingSince\": \"2020-04-15\",\n" +
                        "\t\"dyspneaSince\": null,\n" +
                        "\t\"tirednessSince\": null,\n" +
                        "\t\"throatSince\": null\n" +
                        "}"))
                .andExpect(status().isAccepted());

        db.query("select * from covid_submission order by _created desc limit 1", rs -> {
            assertEquals(code, rs.getString("participant_code"));
            assertEquals("female", rs.getString("sex"));
            assertTrue(rs.getBoolean("feels_healthy"));
            assertFalse(rs.getBoolean("previously_unhealthy"));
            assertEquals("51-60", rs.getString("age_range"));
            assertEquals(LocalDate.of(2020, 3, 23), rs.getObject("when_tested", LocalDate.class));
            assertNull(rs.getObject("symptom_fever", LocalDate.class));
            assertEquals(LocalDate.of(2020, 4, 15), rs.getObject("symptom_coughing", LocalDate.class));
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

    @Test
    void testInvalidAgeRangeErrors() throws Exception
    {
        mockMvc.perform(post("/form")
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .param("sex", "male")
                .param("ageRange", "25-75")
                .param("zip", "9000")
                .param("householdSize", "3")
                .param("phoneDigits", "0056")
                .param("feelsHealthy", "0")
                .param("previouslyUnhealthy", "0")
                .param("hasBeenTested", "0")
                .param("testResult", "negative")
                .param("worksInHealth", "private_practice")
                .param("wasAbroad", "no")
                .param("wasInContactWithCase", "0")
                .param("behavior", "self_isolation")
                .header("user-agent", "test"))
                .andExpect(status().isFound())
                .andExpect(header().string("Location", "https://www.covidtracker.ch/?error=true"));
    }

    @Test
    void saveAllMergedFields() throws Exception
    {
        MvcResult result = mockMvc.perform(post("/form")
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .param("sex", "male")
                .param("ageRange", "31-40")
                .param("zip", "8004")
                .param("householdSize", "2")
                .param("feelsHealthy", "0")
                .param("previouslyUnhealthy", "1")
                .param("hasBeenTested", "1")
                .param("whenTested", "2020-03-10")
                .param("whereTested", "Ticino")
                .param("soughtMedicalAdvice", "0")
                .param("testResult", "positive")
                .param("worksInHealth", "no")
                .param("leavingHomeForWork", "1")
                .param("wasAbroad", "italy")
                .param("wasInContactWithCase", "1")
                .param("dateContacted", "2020-03-05")
                .param("isSmoker", "1")
                .param("comorbidity_highBloodPressure", "1")
                .param("comorbidity_diabetes1", "1")
                .param("comorbidity_diabetes2", "0")
                .param("comorbidity_obesity", "1")
                .param("comorbidity_pregnancy", "0")
                .param("comorbidity_immuneSystem", "0")
                .param("comorbidity_bloodCancer", "1")
                .param("comorbidity_hiv", "0")
                .param("comorbidity_spleen", "1")
                .param("comorbidity_kidney", "1")
                .param("comorbidity_heartDisease", "1")
                .param("comorbidity_liver", "0")
                .param("comorbidity_respiratory", "0")
                .param("comorbidity_neurological", "1")
                .param("comorbidity_organTransplant", "1")
                .param("fever", "1")
                .param("feverSince", "2020-03-07")
                .param("coughing", "1")
                .param("coughingSince", "2020-03-08")
                .param("dyspnea", "1")
                .param("dyspneaSince", "2020-03-08")
                .param("tiredness", "1")
                .param("tirednessSince", "2020-03-06")
                .param("throat", "0")
                .param("throatSince", "")
                .param("nausea", "0")
                .param("nauseaSince", "")
                .param("headache", "0")
                .param("headacheSince", "")
                .param("musclePain", "0")
                .param("musclePainSince", "")
                .param("runnyNose", "0")
                .param("runnyNoseSince", "")
                .param("diarrhea", "1")
                .param("diarrheaSince", "2020-03-15")
                .param("lostTaste", "0")
                .param("lostTasteSince", "")
                .param("previousFever", "1")
                .param("previousCoughing", "1")
                .param("previousDyspnea", "1")
                .param("previousTiredness", "0")
                .param("previousThroat", "0")
                .param("previousNausea", "1")
                .param("previousHeadache", "0")
                .param("previousMusclePain", "1")
                .param("previousRunnyNose", "1")
                .param("previousDiarrhea", "0")
                .param("previousLostTaste", "1")
                .param("behavior", "typical_activity")
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
            assertEquals("31-40", rs.getString("age_range"));
            assertEquals(8004, rs.getInt("zip"));
            assertEquals(2, rs.getInt("household_size"));
            assertFalse(rs.getBoolean("feels_healthy"));
            assertTrue(rs.getBoolean("previously_unhealthy"));
            assertTrue(rs.getBoolean("has_been_tested"));
            assertEquals(LocalDate.of(2020, 3, 10), rs.getObject("when_tested", LocalDate.class));
            assertEquals("Ticino", rs.getString("where_tested"));
            assertFalse(rs.getBoolean("sought_medical_advice"));
            assertEquals("positive", rs.getString("test_result"));
            assertEquals("no", rs.getString("works_in_health"));
            assertTrue(rs.getBoolean("leaving_home_for_work"));
            assertEquals("italy", rs.getString("was_abroad"));
            assertEquals(LocalDate.of(2020, 3, 5), rs.getObject("was_in_contact_with_case", LocalDate.class));
            assertTrue(rs.getBoolean("is_smoker"));
            assertTrue(rs.getBoolean("comorbidity_highBloodPressure"));
            assertTrue(rs.getBoolean("comorbidity_diabetes1"));
            assertFalse(rs.getBoolean("comorbidity_diabetes2"));
            assertTrue(rs.getBoolean("comorbidity_obesity"));
            assertFalse(rs.getBoolean("comorbidity_pregnancy"));
            assertFalse(rs.getBoolean("comorbidity_immuneSystem"));
            assertTrue(rs.getBoolean("comorbidity_bloodCancer"));
            assertFalse(rs.getBoolean("comorbidity_hiv"));
            assertTrue(rs.getBoolean("comorbidity_spleen"));
            assertTrue(rs.getBoolean("comorbidity_kidney"));
            assertTrue(rs.getBoolean("comorbidity_heartDisease"));
            assertFalse(rs.getBoolean("comorbidity_liver"));
            assertFalse(rs.getBoolean("comorbidity_respiratory"));
            assertTrue(rs.getBoolean("comorbidity_neurological"));
            assertTrue(rs.getBoolean("comorbidity_organTransplant"));
            assertEquals(LocalDate.of(2020, 3, 7), rs.getObject("symptom_fever", LocalDate.class));
            assertEquals(LocalDate.of(2020, 3, 8), rs.getObject("symptom_coughing", LocalDate.class));
            assertEquals(LocalDate.of(2020, 3, 8), rs.getObject("symptom_dyspnea", LocalDate.class));
            assertEquals(LocalDate.of(2020, 3, 6), rs.getObject("symptom_tiredness", LocalDate.class));
            assertNull(rs.getString("symptom_throat"));
            assertNull(rs.getString("symptom_nausea"));
            assertNull(rs.getString("symptom_headache"));
            assertNull(rs.getString("symptom_musclePain"));
            assertNull(rs.getString("symptom_runnyNose"));
            assertEquals(LocalDate.of(2020, 3, 15), rs.getObject("symptom_diarrhea", LocalDate.class));
            assertNull(rs.getString("symptom_lostTaste"));
            assertTrue(rs.getBoolean("previously_fever"));
            assertTrue(rs.getBoolean("previously_coughing"));
            assertTrue(rs.getBoolean("previously_dyspnea"));
            assertFalse(rs.getBoolean("previously_tiredness"));
            assertFalse(rs.getBoolean("previously_throat"));
            assertTrue(rs.getBoolean("previously_nausea"));
            assertFalse(rs.getBoolean("previously_headache"));
            assertTrue(rs.getBoolean("previously_musclePain"));
            assertTrue(rs.getBoolean("previously_runnyNose"));
            assertFalse(rs.getBoolean("previously_diarrhea"));
            assertTrue(rs.getBoolean("previously_lostTaste"));
            assertEquals("typical_activity", rs.getString("behavior"));

            assertNotNull(rs.getDate("_created"));
            assertEquals("127.0.0.0", rs.getString("_ip_addr"));
            assertEquals("08bd7b3f7d005739ab6b53fe71548ab5d65ccfca5651e1163e228dd264f3c10a", rs.getString("_ip_hash"));
            assertEquals("88cd2108b5347d973cf39cdf9053d7dd42704876d8c9a9bd8e2d168259d3ddf7", rs.getString("_ua_hash"));
        });
    }

    @Test
    void submittingWithLangRedirectsCorrectly() throws Exception
    {
        mockMvc.perform(post("/form")
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .param("sex", "male")
                .param("yearOfBirth", "1990")
                .param("ageRange", "21-30")
                .param("zip", "9000")
                .param("householdSize", "3")
                .param("phoneDigits", "0056")
                .param("feelsHealthy", "0")
                .param("previouslyUnhealthy", "0")
                .param("hasBeenTested", "0")
                .param("testResult", "negative")
                .param("worksInHealth", "private_practice")
                .param("wasAbroad", "no")
                .param("wasInContactWithCase", "0")
                .param("behavior", "self_isolation")
                .param("lang", "fr")
                .header("user-agent", "test"))
                .andExpect(status().isFound())
                .andExpect(header().string("Location", startsWith("https://www.covidtracker.ch/fr/response.html")));
    }

    @Test
    public void errorReturnsLanguageSpecificURL() throws Exception
    {
        mockMvc.perform(post("/form")
            .contentType(MediaType.APPLICATION_FORM_URLENCODED)
            .param("lang", "fr"))
            .andExpect(status().isFound())
            .andExpect(header().string("Location", "https://www.covidtracker.ch/fr/?error=true"));
    }

    @Test
    void submittingWithInvalidLangFails() throws Exception
    {
        mockMvc.perform(post("/form")
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .param("sex", "male")
                .param("yearOfBirth", "1990")
                .param("ageRange", "21-30")
                .param("zip", "9000")
                .param("householdSize", "3")
                .param("phoneDigits", "0056")
                .param("feelsHealthy", "0")
                .param("previouslyUnhealthy", "0")
                .param("hasBeenTested", "0")
                .param("testResult", "negative")
                .param("worksInHealth", "private_practice")
                .param("wasAbroad", "no")
                .param("wasInContactWithCase", "0")
                .param("behavior", "self_isolation")
                .param("lang", "lunarian")
                .header("user-agent", "test"))
                .andExpect(status().isFound())
                .andExpect(header().string("Location", "https://www.covidtracker.ch/?error=true"));
    }
}
