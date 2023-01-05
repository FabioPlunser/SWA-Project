package at.ac.uibk.swa.controllers.deck_controller;

import at.ac.uibk.swa.models.Card;
import at.ac.uibk.swa.models.Permission;
import at.ac.uibk.swa.models.Person;
import at.ac.uibk.swa.repositories.CardRepository;
import at.ac.uibk.swa.repositories.DeckRepository;
import at.ac.uibk.swa.service.PersonService;
import at.ac.uibk.swa.util.StringGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@ActiveProfiles("test")
@AutoConfigureMockMvc
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class TestDeckControllerGeneral {
    @Autowired
    private PersonService personService;
    @Autowired
    private WebApplicationContext webApplicationContext;
    @SuppressWarnings("SpringJavaInjectionPointsAutowiringInspection")
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    DeckRepository deckRepository;
    @Autowired
    CardRepository cardRepository;

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
        Set<GrantedAuthority> permissions = Set.of(Permission.USER);
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
        long numberOfDecksBefore = deckRepository.count();
        long numberOfCardsBefore = cardRepository.count();

        // when: creating a new deck with 2 cards
        ObjectMapper mapper = new ObjectMapper();
        ObjectNode content = mapper.createObjectNode();
        content.put("name", StringGenerator.deckName());
        content.put("description", StringGenerator.deckDescription());
        content.put("isPublic", true);
        ArrayNode cards = mapper.createArrayNode();
        int numberOfCardsToCreate = 10;
        for (int i = 0; i < numberOfCardsToCreate; i++) {
            ObjectNode card = mapper.createObjectNode();
            card.put("frontText", StringGenerator.cardText());
            card.put("backText", StringGenerator.cardText());
            cards.addPOJO(card);
        }
        content.putPOJO("cards", cards);

        mockMvc.perform(MockMvcRequestBuilders.post("/api/create-deck")
                        .header(HttpHeaders.AUTHORIZATION, token)
                        .content(content.toString())
                        .contentType(MediaType.APPLICATION_JSON)
        // then: response must be ok, number of created decks and cards must be as desired
        ).andExpectAll(
                status().isOk()
        );
        assertEquals(1, deckRepository.count() - numberOfDecksBefore, "Did not create as many decks as expected");
        assertEquals(numberOfCardsToCreate, cardRepository.count() - numberOfCardsBefore, "Did not create as many cards as expected");
    }
}