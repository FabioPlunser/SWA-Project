package at.ac.uibk.swa.controllers.deck_controller;

import at.ac.uibk.swa.models.Card;
import at.ac.uibk.swa.models.Deck;
import at.ac.uibk.swa.models.Permission;
import at.ac.uibk.swa.models.Person;
import at.ac.uibk.swa.repositories.CardRepository;
import at.ac.uibk.swa.repositories.DeckRepository;
import at.ac.uibk.swa.service.PersonService;
import at.ac.uibk.swa.service.UserDeckService;
import at.ac.uibk.swa.util.ArgumentGenerator;
import at.ac.uibk.swa.util.MockAuthContext;
import at.ac.uibk.swa.util.SetupH2Console;
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
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
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

import javax.print.DocFlavor;
import java.util.*;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@ActiveProfiles("test")
@AutoConfigureMockMvc
@ExtendWith({SetupH2Console.class})
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

    private ObjectMapper mapper = new ObjectMapper();

    private Person createUserAndLogin(boolean admin) {
        String username = StringGenerator.username();
        String password = StringGenerator.password();
        Set<GrantedAuthority> permissions = new java.util.HashSet<>();
        if (admin) {
            permissions.add(Permission.ADMIN);
        } else {
            permissions.add(Permission.USER);
        }
        Person person = new Person(username, StringGenerator.email(), password, permissions);
        assertTrue(personService.create(person), "Unable to create user");
        return (Person) MockAuthContext.setLoggedInUser(personService.login(username, password).orElse(null));
    }

    private Deck createDeck(int numberOfCards, boolean published, boolean cardsFlipped, boolean asAdmin) {
        createUserAndLogin(asAdmin);
        Deck deck = new Deck(StringGenerator.deckName(), StringGenerator.deckDescription());
        List<Card> cards = new ArrayList<>();
        for (int i = 0; i < numberOfCards; i++) {
            cards.add(new Card(StringGenerator.cardText(), StringGenerator.cardText(), cardsFlipped));
        }
        deck.setCards(cards);
        assertTrue(userDeckService.create(deck), "Unable to create deck");
        if (published) {
            assertTrue(userDeckService.publish(deck.getDeckId()), "Unable to publish deck");
        }
        MockAuthContext.setLoggedInUser(null);
        return deck;
    }

    private Stream<Arguments> createDeckConfig() {
        return ArgumentGenerator.booleans(5);
    }

    @ParameterizedTest
    @MethodSource("createDeckConfig")
    void createDeck(
            boolean userIsAlsoAdmin,
            boolean published,
            boolean blocked,
            boolean deleted,
            boolean cardsFlipped
    ) throws Exception {
        // given: user created in database, logged in
        String token = "Bearer " + createUserAndLogin(userIsAlsoAdmin).getToken();

        // when: creating a new deck with cards
        ObjectNode content = mapper.createObjectNode();
        String deckName = StringGenerator.deckName();
        String deckDescription = StringGenerator.deckDescription();
        content.put("name", deckName);
        content.put("description", deckDescription);
        content.put("published", published);
        content.put("blocked", blocked);
        content.put("deleted", deleted);
        ArrayNode cards = mapper.createArrayNode();
        int numberOfCardsToCreate = 10;
        List<String> frontTexts = new ArrayList<>();
        List<String> backTexts = new ArrayList<>();
        for (int i = 0; i < numberOfCardsToCreate; i++) {
            ObjectNode card = mapper.createObjectNode();
            String frontText = StringGenerator.cardText();
            frontTexts.add(frontText);
            String backText = StringGenerator.cardText();
            backTexts.add(backText);
            card.put("frontText", frontText);
            card.put("backText", backText);
            card.put("flipped", cardsFlipped);
            cards.addPOJO(card);
        }
        content.putPOJO("cards", cards);

        mockMvc.perform(MockMvcRequestBuilders.post("/api/create-deck")
                        .header(HttpHeaders.AUTHORIZATION, token)
                        .content(content.toString())
                        .contentType(MediaType.APPLICATION_JSON)
        // then: response must be ok, created decks and cards must be as desired
        ).andExpectAll(
                status().isOk()
        );
        Optional<Deck> maybeDeck = deckRepository.findAll().stream().filter(d -> d.getName().equals(deckName)).findFirst();
        assertTrue(maybeDeck.isPresent(), "Unable to find created deck");
        Deck createdDeck = maybeDeck.get();
        assertEquals(deckName, createdDeck.getName(), "Deck got the wrong name");
        assertEquals(deckDescription, createdDeck.getDescription(), "Deck got the wrong description");
        assertEquals(published, createdDeck.isPublished(), "Deck did not get published");
        assertFalse(createdDeck.isBlocked(), "Deck got blocked even though that should not be possible with create-deck");
        assertFalse(createdDeck.isDeleted(), "Deck got deleted even though that should not be possible with create-deck");
        assertEquals(numberOfCardsToCreate, createdDeck.getCards().size(), "Created the wrong number of cards");
        for (int i = 0; i < createdDeck.getCards().size(); i++) {
            assertEquals(frontTexts.get(i), createdDeck.getCards().get(i).getFrontText(), "Card has the wrong front text");
            assertEquals(backTexts.get(i), createdDeck.getCards().get(i).getBackText(), "Card has the wrong back text");
            assertEquals(cardsFlipped, createdDeck.getCards().get(i).isFlipped(), "Card got (not) flipped");
        }
    }

    private Stream<Arguments> updateDeckConfig() {
        return ArgumentGenerator.booleans(7);
    }

    @ParameterizedTest
    @MethodSource("updateDeckConfig")
    void updateDeck(
            boolean initiallyPublished,
            boolean finallyPublished,
            boolean publishAsAdmin,
            boolean cardsInitiallyFlipped,
            boolean cardsFinallyFlipped,
            boolean tryToBlock,
            boolean tryToDelete
    ) throws Exception {
        // given: user created in database, logged in and a given deck
        Deck initialDeck = createDeck(10, initiallyPublished, cardsInitiallyFlipped, publishAsAdmin);

        // when: updating the given deck
        List<Card> initialCards = initialDeck.getCards();
        ObjectNode content = mapper.createObjectNode();
        String updatedDeckName = StringGenerator.deckName();
        String updatedDeckDescription = StringGenerator.deckDescription();
        content.put("deckId", initialDeck.getDeckId().toString());
        content.put("name", updatedDeckName);
        content.put("description", updatedDeckDescription);
        content.put("published", finallyPublished);
        content.put("blocked", tryToBlock);
        content.put("deleted", tryToDelete);
        ArrayNode contentCards = mapper.createArrayNode();
        int numberOfCardsToUpdate = 5;
        int numberOfCardsToDelete = 5;
        List<String> cardFrontTexts = new ArrayList<>();
        List<String> cardBackTexts = new ArrayList<>();
        for (int i = 0; i < numberOfCardsToUpdate; i++) {
            Card card = initialCards.get(i);
            ObjectNode contentCard = mapper.createObjectNode();
            contentCard.put("cardId", card.getCardId().toString());
            String frontText = StringGenerator.cardText();
            String backText = StringGenerator.cardText();
            contentCard.put("frontText", frontText);
            cardFrontTexts.add(frontText);
            contentCard.put("backText", backText);
            cardBackTexts.add(backText);
            contentCard.put("flipped", cardsFinallyFlipped);
            contentCards.addPOJO(contentCard);
        }
        int numberOfCardsToCreate = 10;
        for (int i = 0; i < numberOfCardsToCreate; i++) {
            ObjectNode contentCard = mapper.createObjectNode();
            String frontText = StringGenerator.cardText();
            String backText = StringGenerator.cardText();
            contentCard.put("frontText", frontText);
            cardFrontTexts.add(frontText);
            contentCard.put("backText", backText);
            cardBackTexts.add(backText);
            contentCard.put("flipped", cardsFinallyFlipped);
            contentCards.addPOJO(contentCard);
        }
        content.putPOJO("cards", contentCards);
        long numberOfDecksBefore = deckRepository.count();

        mockMvc.perform(MockMvcRequestBuilders.post("/api/update-deck")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + initialDeck.getCreator().getToken())
                        .content(content.toString())
                        .contentType(MediaType.APPLICATION_JSON)
        // then: response must be ok, created decks and cards must be as desired
        ).andExpectAll(
                status().isOk()
        );

        assertEquals(0, deckRepository.count() - numberOfDecksBefore, "Created a new deck instead of updating the old one");
        Optional<Deck> maybeDeck = deckRepository.findById(initialDeck.getDeckId());
        assertTrue(maybeDeck.isPresent(), "Unable to find updated deck");
        Deck updatedDeck = maybeDeck.get();
        assertEquals(updatedDeckName, updatedDeck.getName(), "Deck name did not get updated");
        assertEquals(updatedDeckDescription, updatedDeck.getDescription(), "Deck description did not get updated");
        assertEquals(finallyPublished, updatedDeck.isPublished(), "Deck did (not) get published");
        assertFalse(updatedDeck.isBlocked(), "Deck got blocked although this should not be possible via update-deck");
        assertFalse(updatedDeck.isDeleted(), "Deck got deleted although this should not be possible via update-deck");

        assertEquals(numberOfCardsToUpdate + numberOfCardsToCreate, updatedDeck.getCards().size(), "Found an unexpected number of cards");

        for (int i = 0; i < numberOfCardsToUpdate; i++) {
            assertTrue(updatedDeck.getCards().contains(initialDeck.getCards().get(i)), "Did not find a card that should have been updated");
        }
        for (int i = numberOfCardsToUpdate; i < numberOfCardsToUpdate + numberOfCardsToDelete; i++) {
            assertFalse(updatedDeck.getCards().contains(initialDeck.getCards().get(i)), "Found a card that should have been deleted");
        }

        for (int i = 0; i < numberOfCardsToUpdate + numberOfCardsToCreate; i++) {
            assertEquals(cardFrontTexts.get(i), updatedDeck.getCards().get(i).getFrontText(), "Front text of card not correct");
            assertEquals(cardBackTexts.get(i), updatedDeck.getCards().get(i).getBackText(), "Back text of card not correct");
            assertEquals(cardsFinallyFlipped, updatedDeck.getCards().get(i).isFlipped(), "Card did (not) get flipped");
        }
    }
}