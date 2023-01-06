package at.ac.uibk.swa.controllers.card_controller;

import at.ac.uibk.swa.models.Card;
import at.ac.uibk.swa.models.Deck;
import at.ac.uibk.swa.models.Permission;
import at.ac.uibk.swa.models.Person;
import at.ac.uibk.swa.service.PersonService;
import at.ac.uibk.swa.service.UserDeckService;
import at.ac.uibk.swa.util.MockAuthContext;
import at.ac.uibk.swa.util.SetupH2Console;
import at.ac.uibk.swa.util.StringGenerator;
import org.h2.tools.Server;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import javax.print.DocFlavor;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

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
    @SuppressWarnings("SpringJavaInjectionPointsAutowiringInspection")
    @Autowired
    private MockMvc mockMvc;

    private Person createUserAndLogin() {
        String username = StringGenerator.username();
        String password = StringGenerator.password();
        Person person = new Person(username, StringGenerator.email(), password, Set.of(Permission.USER, Permission.ADMIN));
        assertTrue(personService.create(person), "Unable to create user");
        return (Person) MockAuthContext.setLoggedInUser(personService.login(username, password).orElse(null));
    }

    private Deck createDeck() {
        createUserAndLogin();
        Deck deck = new Deck(StringGenerator.deckName(), StringGenerator.deckDescription());
        int numberOfCards = 1;
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

    @Test
    public void getAllCardsOfDeck() throws Exception {
        Deck deck = createDeck();
        String token = "Bearer " + deck.getCreator().getToken().toString();

        mockMvc.perform(MockMvcRequestBuilders.get("/api/get-cards-of-deck")
                .header(HttpHeaders.AUTHORIZATION, token)
                .param("deckId", deck.getDeckId().toString())
                .contentType(MediaType.APPLICATION_JSON)
        ).andExpectAll(status().isOk())
        .andDo(print());
    }
}
