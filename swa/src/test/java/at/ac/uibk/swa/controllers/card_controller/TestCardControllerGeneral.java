package at.ac.uibk.swa.controllers.card_controller;

import at.ac.uibk.swa.models.Card;
import at.ac.uibk.swa.models.Deck;
import at.ac.uibk.swa.models.Permission;
import at.ac.uibk.swa.models.Person;
import at.ac.uibk.swa.service.AdminDeckService;
import at.ac.uibk.swa.service.PersonService;
import at.ac.uibk.swa.service.UserDeckService;
import at.ac.uibk.swa.util.*;
import org.hamcrest.Matchers;
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

import java.util.*;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@ActiveProfiles("test")
@AutoConfigureMockMvc
@ExtendWith({SetupH2Console.class})
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class TestCardControllerGeneral {
    @Autowired
    private PersonService personService;
    @Autowired
    private UserDeckService userDeckService;
    @Autowired
    private AdminDeckService adminDeckService;
    @SuppressWarnings("SpringJavaInjectionPointsAutowiringInspection")
    @Autowired
    private MockMvc mockMvc;

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

    private Deck createDeck(int numberOfCards, boolean published, boolean asAlsoAdmin) {
        createUserAndLogin(asAlsoAdmin);
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
        return deck;
    }

    private Stream<Arguments> getAllCardsOfDeckConfig() {
        return ArgumentGenerator.booleans(6);
    }

    @ParameterizedTest
    @MethodSource("getAllCardsOfDeckConfig")
    public void getAllCardsOfDeck(
            boolean published,
            boolean blocked,
            boolean deleted,
            boolean creatorIsAlsoAdmin,
            boolean userIsOwner,
            boolean userIsAdminIfNotOwner
    ) throws Exception {
        // given: a deck created by a user
        Deck deck = createDeck(10, published, creatorIsAlsoAdmin);

        if (blocked) {
            createUserAndLogin(true);
            adminDeckService.block(deck.getDeckId());
        }

        if (deleted) {
            MockAuthContext.setLoggedInUser(deck.getCreator());
            userDeckService.delete(deck.getDeckId());
        }

        String token = "Bearer ";
        if (userIsOwner) {
             token += deck.getCreator().getToken();
        } else {
            token += createUserAndLogin(userIsAdminIfNotOwner).getToken();
        }

        boolean userIsAdmin = (userIsOwner && creatorIsAlsoAdmin) || (!userIsOwner && userIsAdminIfNotOwner);
        boolean expectCards = !deleted && !(blocked && !userIsAdmin) && (published || userIsOwner || userIsAdmin);

        // when: getting all cards for that deck
        mockMvc.perform(MockMvcRequestBuilders.get("/api/get-cards-of-deck")
                .header(HttpHeaders.AUTHORIZATION, token)
                .param("deckId", deck.getDeckId().toString())
                .contentType(MediaType.APPLICATION_JSON)
        )
        // then: status must be ok, all cards must be contained
        .andExpectAll(
                status().isOk(),
                jsonPath("$.items").isArray(),
                jsonPath("$.items").value(Matchers.hasSize(expectCards ? deck.getCards().size() : 0))
        );
    }
}
