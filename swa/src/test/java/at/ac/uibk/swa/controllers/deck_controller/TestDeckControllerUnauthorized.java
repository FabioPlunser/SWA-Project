package at.ac.uibk.swa.controllers.deck_controller;

import at.ac.uibk.swa.models.Permission;
import at.ac.uibk.swa.models.Person;
import at.ac.uibk.swa.service.PersonService;
import at.ac.uibk.swa.util.StringGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.fasterxml.jackson.databind.SerializationFeature;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * (Most) deck controller methods should only be accessible by authorized persons
 * The included tests try to access these methods without a token
 * It is in all cases expected that status code 401 (Unauthorized) is returned
 *
 */
@SpringBootTest
@ActiveProfiles("test")
@AutoConfigureMockMvc
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class TestDeckControllerUnauthorized {
    @Autowired
    private PersonService personService;
    @Autowired
    private WebApplicationContext webApplicationContext;
    @SuppressWarnings("SpringJavaInjectionPointsAutowiringInspection")
    @Autowired
    private MockMvc mockMvc;

    @Test
    void createDeck() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.post("/api/createDeck"))
                .andExpectAll(
                        status().isUnauthorized()
                );
    }

    @Test
    void updateDeck() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.post("/api/updateDeck"))
                .andExpectAll(
                    status().isUnauthorized()
                );
    }

    @Test
    void setPublicity() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.put("/api/setPublicity"))
                .andExpectAll(
                        status().isUnauthorized()
                );
    }

    @Test
    void deleteDeck() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.delete("/api/deleteDeck"))
                .andExpectAll(
                        status().isUnauthorized()
                );
    }

    @Test
    void getUserDecks() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/api/getUserDecks"))
                .andExpectAll(
                        status().isUnauthorized()
                );
    }

    @Test
    void getPublishedDecks() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/api/getPublishedDecks"))
                .andExpectAll(
                        status().isUnauthorized()
                );
    }

    @Test
    void getAllDecks() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/api/getAllDecks"))
                .andExpectAll(
                        status().isUnauthorized()
                );
    }

    @Test
    void getAllCardsToLearn() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/api/getAllCardsToLearn"))
                .andExpectAll(
                        status().isUnauthorized()
                );
    }
}