package at.ac.uibk.swa.controllers;

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

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@ActiveProfiles("test")
@AutoConfigureMockMvc
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class DeckControllerTest {
    @Autowired
    private PersonService personService;
    @Autowired
    private WebApplicationContext webApplicationContext;
    @SuppressWarnings("SpringJavaInjectionPointsAutowiringInspection")
    @Autowired
    private MockMvc mockMvc;

    private ObjectWriter writer;

    @BeforeAll
    public void setup() {
        ObjectMapper mapper = new ObjectMapper();
        mapper.configure(SerializationFeature.WRAP_ROOT_VALUE, false);
        this.writer = mapper.writer().withDefaultPrettyPrinter();
    }

    private String toJson(Object object) throws JsonProcessingException {
        return writer.writeValueAsString(object);
    }

    private String createUserAndGetToken() throws Exception {
        String username = StringGenerator.username();
        String password = StringGenerator.password();
        Set<Permission> permissions = Set.of(Permission.USER);
        Person person = new Person(username, StringGenerator.email(), password, permissions);
        assertTrue(personService.create(person), "Unable to create user");
        Optional<Person> maybePerson = personService.login(username, password);
        assertTrue(maybePerson.isPresent(), "Unable to login");
        return "Bearer " + maybePerson.get().getToken().toString();
    }

    @Test
    void createDeck() throws Exception {
        // given: user created in database, logged in
        String token = createUserAndGetToken();

        // when: creating a new deck
        mockMvc.perform(MockMvcRequestBuilders.post("/api/createDeck")
                        .header(HttpHeaders.AUTHORIZATION, token)
                        .param("name", StringGenerator.deckName())
                        .param("description", StringGenerator.deckDescription())
                        .contentType(MediaType.APPLICATION_JSON)
        // then:
        ).andExpectAll(
                status().isOk()
        );
    }
}