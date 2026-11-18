package com.impilohealth.cycle;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest
@AutoConfigureMockMvc
class CycleControllerTest {

    @Autowired MockMvc mvc;
    @Autowired ObjectMapper json;

    private String token(String email) throws Exception {
        String body = mvc.perform(post("/api/auth/register").contentType(MediaType.APPLICATION_JSON)
                        .content("{\"fullName\":\"Cycle User\",\"email\":\"" + email
                                + "\",\"password\":\"password123\",\"gender\":\"FEMALE\"}"))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();
        return "Bearer " + json.readTree(body).get("token").asText();
    }

    private void logPeriod(String token, String start) throws Exception {
        mvc.perform(post("/api/cycle/periods").header("Authorization", token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"startDate\":\"" + start + "\",\"flow\":\"MEDIUM\"}"))
                .andExpect(status().isCreated());
    }

    @Test
    void requiresAuthentication() throws Exception {
        mvc.perform(get("/api/cycle/prediction")).andExpect(status().isUnauthorized());
    }

    @Test
    void noPeriodsMeansPredictionUnavailable() throws Exception {
        String t = token("cycle-empty@example.com");
        mvc.perform(get("/api/cycle/prediction").header("Authorization", t))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.available").value(false));
    }

    @Test
    void logPeriodsThenPredict() throws Exception {
        String t = token("cycle-predict@example.com");
        logPeriod(t, "2026-09-05");
        logPeriod(t, "2026-10-03");

        mvc.perform(get("/api/cycle/prediction").header("Authorization", t))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.available").value(true))
                .andExpect(jsonPath("$.averageCycleLength").value(28))
                .andExpect(jsonPath("$.nextPeriodStart").value("2026-10-31"));

        mvc.perform(get("/api/cycle/periods").header("Authorization", t))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].startDate").value("2026-10-03"));
    }

    @Test
    void duplicateStartDateIsConflict() throws Exception {
        String t = token("cycle-dup@example.com");
        logPeriod(t, "2026-10-03");
        mvc.perform(post("/api/cycle/periods").header("Authorization", t)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"startDate\":\"2026-10-03\"}"))
                .andExpect(status().isConflict());
    }

    @Test
    void endBeforeStartIsRejected() throws Exception {
        String t = token("cycle-bad@example.com");
        mvc.perform(post("/api/cycle/periods").header("Authorization", t)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"startDate\":\"2026-10-10\",\"endDate\":\"2026-10-05\"}"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void futureStartDateIsRejected() throws Exception {
        String t = token("cycle-future@example.com");
        mvc.perform(post("/api/cycle/periods").header("Authorization", t)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"startDate\":\"2999-01-01\"}"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void symptomsAreUpsertedPerDayAndQueryableByRange() throws Exception {
        String t = token("cycle-symptoms@example.com");
        String day = "2026-10-05";

        mvc.perform(post("/api/cycle/symptoms").header("Authorization", t)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"date\":\"" + day + "\",\"flow\":\"HEAVY\",\"symptoms\":[\"CRAMPS\",\"FATIGUE\"]}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.symptoms.length()").value(2));

        // Re-log same day replaces, does not duplicate
        mvc.perform(post("/api/cycle/symptoms").header("Authorization", t)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"date\":\"" + day + "\",\"flow\":\"LIGHT\",\"symptoms\":[\"HEADACHE\"]}"))
                .andExpect(status().isOk());

        mvc.perform(get("/api/cycle/symptoms").param("from", "2026-10-01").param("to", "2026-10-31")
                        .header("Authorization", t))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].flow").value("LIGHT"))
                .andExpect(jsonPath("$[0].symptoms[0]").value("HEADACHE"));
    }

    @Test
    void usersCannotSeeEachOthersData() throws Exception {
        String a = token("cycle-a@example.com");
        String b = token("cycle-b@example.com");
        logPeriod(a, "2026-10-03");
        mvc.perform(get("/api/cycle/periods").header("Authorization", b))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(0));
    }
}