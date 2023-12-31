package at.ac.uibk.swa.service.admin_deck_service;

import at.ac.uibk.swa.models.Deck;
import at.ac.uibk.swa.models.Permission;
import at.ac.uibk.swa.models.Person;
import at.ac.uibk.swa.service.AdminDeckService;
import at.ac.uibk.swa.service.PersonService;
import at.ac.uibk.swa.service.UserDeckService;
import at.ac.uibk.swa.util.MockAuthContext;
import at.ac.uibk.swa.util.StringGenerator;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
public class TestAdminDeckServiceGeneral {
    @Autowired
    AdminDeckService adminDeckService;
    @Autowired
    UserDeckService userDeckService;
    @Autowired
    PersonService personService;

    private Person createUserAndLogin() {
        Person person = new Person(StringGenerator.username(), StringGenerator.email(), StringGenerator.password(), Set.of(Permission.USER));
        assertTrue(personService.create(person), "Unable to create user");
        return (Person) MockAuthContext.setLoggedInUser(person);
    }

    private Deck createDeck() {
        Deck deck = new Deck(StringGenerator.deckName(), StringGenerator.deckDescription());
        assertTrue(userDeckService.create(deck), "Unable to create deck");
        return deck;
    }

    @Test
    public void getDeckByIdAsAdmin() {
        // given: a deck in the repository
        createUserAndLogin();
        Deck deck = createDeck();
        UUID id = deck.getDeckId();
        MockAuthContext.setLoggedInUser(null);

        // when: trying to load that deck by id from the repository
        Optional<Deck> maybeDeck = adminDeckService.findById(id);

        // then: retrieved deck must be correct
        assertTrue(maybeDeck.isPresent(), "Unable to find deck");
        assertEquals(deck, maybeDeck.get(), "Got deck " + maybeDeck.get() + " when deck " + deck + " was expected");
    }

    @Test
    public void blockDeck() {
        // given: a deck in the repository
        createUserAndLogin();
        Deck deck = createDeck();
        UUID id = deck.getDeckId();
        MockAuthContext.setLoggedInUser(null);

        // when: blocking that deck
        assertTrue(adminDeckService.block(deck.getDeckId()), "Unable to block deck");

        // then: deck must be set as blocked within the repository
        Optional<Deck> maybeDeck = adminDeckService.findById(id);
        assertTrue(maybeDeck.isPresent(), "Unable to find deck");
        assertTrue(maybeDeck.get().isBlocked(), "Deck was not blocked");
    }

    @Test
    public void unblockDeck() {
        // given: a deck in the repository, that was blocked
        createUserAndLogin();
        Deck deck = createDeck();
        UUID id = deck.getDeckId();
        MockAuthContext.setLoggedInUser(null);
        assertTrue(adminDeckService.block(deck.getDeckId()), "Unable to block deck");

        // when: unblocking that deck
        assertTrue(adminDeckService.unblock(deck.getDeckId()), "Unable to unblock deck");

        // then: deck must be set as unblocked within the repository
        Optional<Deck> maybeDeck = adminDeckService.findById(id);
        assertTrue(maybeDeck.isPresent(), "Unable to find deck");
        assertFalse(maybeDeck.get().isBlocked(), "Deck was not blocked");
    }

    @Test
    public void findAllDecksAsAdmin() {
        // given: a number of decks in the repository, where one was deleted, one was blocked and one was published
        int numberOfDecks = 4;
        List<Deck> decks = new ArrayList<>();
        Person creator = createUserAndLogin();
        for (int i = 0; i < numberOfDecks; i++) {
            decks.add(createDeck());
        }
        assertTrue(userDeckService.delete(decks.get(0).getDeckId()));
        Deck deletedDeck = decks.remove(0);
        assertTrue(userDeckService.publish(decks.get(0).getDeckId()));
        MockAuthContext.setLoggedInUser(null);
        assertTrue(adminDeckService.block(decks.get(1).getDeckId()));

        // when: loading all decks within the repository
        List<Deck> loadedDecks = adminDeckService.findAll();

        // then: all decks except the deleted one must be found
        for (Deck deck : decks) {
            assertTrue(loadedDecks.contains(deck), "Unable to find deck " + deck);
        }
        assertFalse(loadedDecks.contains(deletedDeck), "Found deleted deck");
    }
}
