package at.ac.uibk.swa.controllers.deck_controller;

import at.ac.uibk.swa.models.Card;
import at.ac.uibk.swa.models.Deck;
import at.ac.uibk.swa.models.Permission;
import at.ac.uibk.swa.models.Person;
import at.ac.uibk.swa.repositories.DeckRepository;
import at.ac.uibk.swa.service.AdminDeckService;
import at.ac.uibk.swa.service.PersonService;
import at.ac.uibk.swa.service.UserDeckService;
import at.ac.uibk.swa.util.ArgumentGenerator;
import at.ac.uibk.swa.util.MockAuthContext;
import at.ac.uibk.swa.util.SetupH2Console;
import at.ac.uibk.swa.util.StringGenerator;
import org.hamcrest.Matchers;
import org.hamcrest.core.IsNot;
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

import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@ActiveProfiles("test")
@AutoConfigureMockMvc
@ExtendWith({SetupH2Console.class})
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class TestDeckControllerGetDecks {
    @Autowired
    private PersonService personService;
    @Autowired
    private UserDeckService userDeckService;
    @Autowired
    private AdminDeckService adminDeckService;
    @SuppressWarnings("SpringJavaInjectionPointsAutowiringInspection")
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private DeckRepository deckRepository;

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

    private Deck createDeck(int numberOfCards, boolean published, boolean blocked, boolean deleted, Person subscriber) {
        createUserAndLogin(false);
        Deck deck = new Deck(StringGenerator.deckName(), StringGenerator.deckDescription());
        List<Card> cards = new ArrayList<>();
        for (int i = 0; i < numberOfCards; i++) {
            cards.add(new Card(StringGenerator.cardText(), StringGenerator.cardText(), false));
        }
        deck.setCards(cards);
        assertTrue(userDeckService.create(deck), "Unable to create deck");
        if (published) {
            assertTrue(userDeckService.publish(deck.getDeckId()), "Unable to publish deck");
        }
        MockAuthContext.setLoggedInUser(null);
        if (published && subscriber != null) {
            MockAuthContext.setLoggedInUser(subscriber);
            userDeckService.subscribe(deck.getDeckId());
            MockAuthContext.setLoggedInUser(null);
        }
        if (blocked) {
            blockDeck(deck);
        }
        if (deleted) {
            deleteDeck(deck);
        }
        return deck;
    }

    private void blockDeck(Deck deck ) {
        createUserAndLogin(true);
        adminDeckService.block(deck.getDeckId());
        MockAuthContext.setLoggedInUser(null);
    }

    private void deleteDeck(Deck deck) {
        MockAuthContext.setLoggedInUser(deck.getCreator());
        userDeckService.delete(deck.getDeckId());
        MockAuthContext.setLoggedInUser(null);
    }

    @Test
    public void getPublishedDecks() throws Exception {
        // given: decks, where the logged in user is subscribed to half of them
        Person person = createUserAndLogin(false);
        List<Boolean[]> configs = ArgumentGenerator.booleans(4).map(a -> (Boolean[]) a.get()).toList();
        int numberOfDecksPerConfig = 4;
        for (Boolean[] config : configs) {
            boolean subscribed = config[0];
            boolean published = config[1];
            boolean blocked = config[2];
            boolean deleted = config[3];
            for (int i = 0; i < numberOfDecksPerConfig; i++) {
                createDeck(2, published, blocked, deleted, subscribed ? person : null);
            }
        }
        List<Deck> decksToFind = deckRepository.findAll().stream()
                .filter(Predicate.not(Deck::isDeleted))
                .filter(Predicate.not(Deck::isBlocked))
                .filter(Deck::isPublished)
                .filter(d -> !person.getSavedDecks().contains(d))
                .toList();

        // when: loading all decks available for subscription
        mockMvc.perform(MockMvcRequestBuilders.get("/api/get-published-decks")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + person.getToken())
                        .contentType(MediaType.APPLICATION_JSON)
        // then: returned decks must be as expected
        ).andExpectAll(
                status().isOk(),
                jsonPath("$.items").isArray(),
                jsonPath("$.items").value(Matchers.hasSize(decksToFind.size())),
                jsonPath("$.items[*].deckId").value(
                        Matchers.containsInAnyOrder(
                                decksToFind.stream()
                                        .map(d -> d.getDeckId().toString())
                                        .toArray(String[]::new)
                        )
                )
        );
    }

    private Stream<Arguments> getCreatedAndSubscribedDecksConfig() {
        return ArgumentGenerator.booleans(3);
    }

    @ParameterizedTest
    @MethodSource("getCreatedAndSubscribedDecksConfig")
    public void getCreatedAndSubscribedDecks (
            boolean unpublish,
            boolean block,
            boolean delete
    ) throws Exception {
        // given: one deck to which a user has subscribed and one deck, that the user has created
        Deck createdDeck = createDeck(1, true, block, delete, null);
        Person person = createdDeck.getCreator();
        String expectedDescription = null;
        Deck subscribedDeck = createDeck(1, true, block, delete, person);
        List<Deck> decks = new ArrayList<>(Arrays.asList(createdDeck, subscribedDeck));
        for (Deck deck : decks) {
            if (unpublish) {
                MockAuthContext.setLoggedInUser(deck.getCreator());
                userDeckService.unpublish(deck.getDeckId());
                expectedDescription = "unpublished";
            }
            if (block) {
                createUserAndLogin(true);
                adminDeckService.block(deck.getDeckId());
                expectedDescription = "blocked";
            }
            if (delete) {
                MockAuthContext.setLoggedInUser(deck.getCreator());
                userDeckService.delete(deck.getDeckId());
                expectedDescription = "deleted";
            }
        }
        MockAuthContext.setLoggedInUser(null);

        // when: loading all decks to which the given user has subscribed
        mockMvc.perform(MockMvcRequestBuilders.get("/api/get-subscribed-decks")
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + person.getToken())
                .contentType(MediaType.APPLICATION_JSON)
        // then: only the subscribed deck must be returned, description must be changed if applicable
        ).andExpectAll(
                status().isOk(),
                jsonPath("$.items").isArray(),
                jsonPath("$.items").value(Matchers.hasSize(1)),
                jsonPath("$.items[0].deckId").value(Matchers.is(subscribedDeck.getDeckId().toString())),
                jsonPath("$.items[0].description").value(
                        (unpublish || block || delete) ?
                                Matchers.containsString(expectedDescription) :
                                Matchers.is(subscribedDeck.getDescription())
                )
        );

        // when: loading all decks that the given user has created
        mockMvc.perform(MockMvcRequestBuilders.get("/api/get-created-decks")
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + person.getToken())
                .contentType(MediaType.APPLICATION_JSON)
        // then: only the created decks must be returned, description must be changed if applicable
        ).andExpectAll(
                status().isOk(),
                jsonPath("$.items").isArray(),
                jsonPath("$.items").value(Matchers.hasSize(delete ? 0 : 1)),
                jsonPath("$.items[*].deckId").value(
                        delete ?
                                Matchers.anything() :
                                Matchers.contains(Matchers.containsString(createdDeck.getDeckId().toString()))
                ),
                jsonPath("$.items[*].description").value(
                        delete ?
                                Matchers.anything() :
                                Matchers.contains(block ?
                                        Matchers.containsString(expectedDescription) :
                                        Matchers.is(createdDeck.getDescription())
                                )
                )
        );
    }
}
