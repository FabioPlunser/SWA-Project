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
import java.util.Set;
import java.util.function.Predicate;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@ActiveProfiles("test")
@AutoConfigureMockMvc
@ExtendWith({SetupH2Console.class})
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class TestDeckControllerFindAll {
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
}
