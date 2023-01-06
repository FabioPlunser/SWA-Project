package at.ac.uibk.swa.controllers.deck_controller;

import at.ac.uibk.swa.models.Card;
import at.ac.uibk.swa.models.Deck;
import at.ac.uibk.swa.models.Permission;
import at.ac.uibk.swa.models.Person;
import at.ac.uibk.swa.repositories.CardRepository;
import at.ac.uibk.swa.repositories.DeckRepository;
import at.ac.uibk.swa.service.PersonService;
import at.ac.uibk.swa.service.UserDeckService;
import at.ac.uibk.swa.util.MockAuthContext;
import at.ac.uibk.swa.util.StringGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import jdk.jshell.spi.ExecutionControlProvider;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.mockito.Mock;
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

import java.util.*;

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
    @SuppressWarnings("SpringJavaInjectionPointsAutowiringInspection")
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    UserDeckService userDeckService;
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

    @Test
    void updateDeck() throws Exception {
        // given: user created in database, logged in and a given deck
        String token = createUserAndGetToken();
        String deckName = StringGenerator.deckName();
        Deck deck = new Deck(deckName, StringGenerator.deckDescription());
        List<Card> cards = new ArrayList<>();
        int numberOfCards = 10;
        for (int i = 0; i < numberOfCards; i++) {
            cards.add(new Card(StringGenerator.cardText(), StringGenerator.cardText(), false));
        }
        deck.setCards(cards);
        ObjectMapper mapper = new ObjectMapper();
        ObjectWriter writer = mapper.writer().withDefaultPrettyPrinter();
        String json = writer.writeValueAsString(deck);
        mockMvc.perform(MockMvcRequestBuilders.post("/api/create-deck")
                        .header(HttpHeaders.AUTHORIZATION, token)
                        .content(json)
                        .contentType(MediaType.APPLICATION_JSON)
        ).andExpectAll(
                status().isOk()
        );
        long numberOfDecksBefore = deckRepository.count();
        long numberOfCardsBefore = cardRepository.count();
        // TODO: get deck id from create-deck response
        UUID deckId = deckRepository.findAll().stream().filter(d -> d.getName().equals(deckName)).findFirst().orElseThrow().getDeckId();

        // when: updating the given deck
        String updatedText = "updated";
        Optional<Deck> maybeDeck = deckRepository.findById(deckId);
        assertTrue(maybeDeck.isPresent(), "Unable to find created deck");
        Deck createdDeck = maybeDeck.get();
        List<Card> createdCards = createdDeck.getCards();
        ObjectNode content = mapper.createObjectNode();
        content.put("deckId", deckId.toString());
        content.put("name", updatedText);
        content.put("description", updatedText);
        content.put("isPublic", updatedText);
        ArrayNode contentCards = mapper.createArrayNode();
        int numberOfCardsToUpdate = 5;
        int numberOfCardsToDelete = numberOfCards - numberOfCardsToUpdate;
        for (int i = 0; i < numberOfCardsToUpdate; i++) {
            Card card = createdCards.get(i);
            ObjectNode contentCard = mapper.createObjectNode();
            contentCard.put("cardId", card.getCardId().toString());
            contentCard.put("frontText", updatedText);
            contentCard.put("backText", updatedText);
            contentCard.put("flipped", true);
            contentCards.addPOJO(contentCard);
        }
        int numberOfCardsToCreate = 10;
        for (int i = 0; i < numberOfCardsToCreate; i++) {
            ObjectNode contentCard = mapper.createObjectNode();
            contentCard.put("frontText", StringGenerator.cardText());
            contentCard.put("backText", StringGenerator.cardText());
            contentCards.addPOJO(contentCard);
        }
        content.putPOJO("cards", contentCards);

        mockMvc.perform(MockMvcRequestBuilders.post("/api/update-deck")
                        .header(HttpHeaders.AUTHORIZATION, token)
                        .content(content.toString())
                        .contentType(MediaType.APPLICATION_JSON)
        // then: response must be ok, number of created decks and cards must be as desired
        ).andExpectAll(
                status().isOk()
        );

        assertEquals(0, deckRepository.count() - numberOfDecksBefore, "Created a new deck instead of updating the old one");
        maybeDeck = deckRepository.findById(deckId);
        assertTrue(maybeDeck.isPresent(), "Unable to find updated deck");
        Deck updatedDeck = maybeDeck.get();
        assertEquals(updatedText, updatedDeck.getName(), "Deck name did not get updated");
        assertEquals(updatedText, updatedDeck.getDescription(), "Deck description did not get updated");
        for (int i = 0; i < numberOfCardsToUpdate; i++) {
            assertEquals(createdDeck.getCards().get(i), updatedDeck.getCards().get(i), "Did not find a card that was updated");
            assertEquals(updatedText, updatedDeck.getCards().get(i).getFrontText(), "Front text did not get updated");
            assertEquals(updatedText, updatedDeck.getCards().get(i).getBackText(), "Back text did not get updated");
            assertTrue(updatedDeck.getCards().get(i).isFlipped(), "Card did not get flipped");
        }
        for (int i = numberOfCardsToUpdate; i < numberOfCards; i++) {
            assertFalse(updatedDeck.getCards().contains(createdDeck.getCards().get(i)), "Found a card that should have been deleted");
        }
        assertEquals(numberOfCardsToCreate - numberOfCardsToDelete, cardRepository.count() - numberOfCardsBefore, "Did not create as many cards as expected");

        mockMvc.perform(MockMvcRequestBuilders.get("/api/get-cards-of-deck")
                .header(HttpHeaders.AUTHORIZATION, token)
                .param("deckId", updatedDeck.getDeckId().toString())
                .contentType(MediaType.APPLICATION_JSON)
        ).andExpectAll(status().isOk()).andDo(print());
    }
}