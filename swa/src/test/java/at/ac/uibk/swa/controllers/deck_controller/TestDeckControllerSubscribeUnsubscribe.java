package at.ac.uibk.swa.controllers.deck_controller;

import at.ac.uibk.swa.models.Card;
import at.ac.uibk.swa.models.Deck;
import at.ac.uibk.swa.models.Permission;
import at.ac.uibk.swa.models.Person;
import at.ac.uibk.swa.repositories.DeckRepository;
import at.ac.uibk.swa.service.PersonService;
import at.ac.uibk.swa.service.UserDeckService;
import at.ac.uibk.swa.util.ArgumentGenerator;
import at.ac.uibk.swa.util.MockAuthContext;
import at.ac.uibk.swa.util.SetupH2Console;
import at.ac.uibk.swa.util.StringGenerator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@ActiveProfiles("test")
@AutoConfigureMockMvc
@ExtendWith({SetupH2Console.class})
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class TestDeckControllerSubscribeUnsubscribe {
    @Autowired
    private PersonService personService;
    @Autowired
    private UserDeckService userDeckService;
    @SuppressWarnings("SpringJavaInjectionPointsAutowiringInspection")
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private DeckRepository deckRepository;

    private Person createUserAndLogin(boolean alsoAdmin) {
        String username = StringGenerator.username();
        String password = StringGenerator.password();
        Set<GrantedAuthority> permissions = new java.util.HashSet<>(Set.of(Permission.USER));
        if (alsoAdmin) {
            permissions.add(Permission.ADMIN);
        }
        Person person = new Person(username, StringGenerator.email(), password, permissions);
        assertTrue(personService.create(person), "Unable to create user");
        return (Person) MockAuthContext.setLoggedInUser(personService.login(username, password).orElse(null));
    }

    private Deck createDeck(int numberOfCards, boolean asAlsoAdmin) {
        createUserAndLogin(asAlsoAdmin);
        Deck deck = new Deck(StringGenerator.deckName(), StringGenerator.deckDescription());
        List<Card> cards = new ArrayList<>();
        for (int i = 0; i < numberOfCards; i++) {
            cards.add(new Card(StringGenerator.cardText(), StringGenerator.cardText(), false));
        }
        deck.setCards(cards);
        assertTrue(userDeckService.create(deck), "Unable to create deck");
        assertTrue(userDeckService.publish(deck.getDeckId()), "Unable to publish deck");
        MockAuthContext.setLoggedInUser(null);
        return deck;
    }

    private Stream<Arguments> subscribeUnsubscribeConfig() {
        return ArgumentGenerator.booleans(2);
    }

    @ParameterizedTest
    @MethodSource("subscribeUnsubscribeConfig")
    public void subscribeToDeck(
            boolean creatorIsAlsoAdmin,
            boolean subscriberIsAlsoAdmin
    ) throws Exception {
        // given: a public deck created by a user and another user
        Deck deck = createDeck(1, creatorIsAlsoAdmin);
        Person person = createUserAndLogin(subscriberIsAlsoAdmin);

        // when: trying to subscribe to that deck
        mockMvc.perform(MockMvcRequestBuilders.post("/api/subscribe-deck")
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + person.getToken())
                .param("deckId", deck.getDeckId().toString())
                .contentType(MediaType.APPLICATION_JSON)
        // then: status must be ok and user must have deck added to saved decks
        ).andExpectAll(
                status().isOk()
        );
        Optional<Person> maybeSubscriber = personService.findById(person.getPersonId());
        assertTrue(maybeSubscriber.isPresent(), "Unable to load subscriber from repository");
        Person subscriber = maybeSubscriber.get();
        assertEquals(1, subscriber.getSavedDecks().size(), "Found more/less saved decks than expected");
        assertTrue(subscriber.getSavedDecks().contains(deck), "Did not find the expected deck");
    }

    @ParameterizedTest
    @MethodSource("subscribeUnsubscribeConfig")
    public void unsubscribeFromDeck(
            boolean creatorIsAlsoAdmin,
            boolean subscriberIsAlsoAdmin
    ) throws Exception {
        // given: a public deck created by a user and another user that has subscribed to that deck
        Deck deck = createDeck(1, creatorIsAlsoAdmin);
        Person person = createUserAndLogin(subscriberIsAlsoAdmin);
        assertTrue(userDeckService.subscribe(deck.getDeckId()), "Unable to subscribe to deck");

        // when: trying to unsubscribe from that deck
        mockMvc.perform(MockMvcRequestBuilders.post("/api/unsubscribe-deck")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + person.getToken())
                        .param("deckId", deck.getDeckId().toString())
                        .contentType(MediaType.APPLICATION_JSON)
        // then: status must be ok and user must have deck removed from saved decks
        ).andExpectAll(
                status().isOk()
        );
        Optional<Person> maybeSubscriber = personService.findById(person.getPersonId());
        assertTrue(maybeSubscriber.isPresent(), "Unable to load subscriber from repository");
        Person subscriber = maybeSubscriber.get();
        assertEquals(0, subscriber.getSavedDecks().size(), "Found more saved decks than expected");
        assertFalse(subscriber.getSavedDecks().contains(deck), "Still found the deck");
    }
}
