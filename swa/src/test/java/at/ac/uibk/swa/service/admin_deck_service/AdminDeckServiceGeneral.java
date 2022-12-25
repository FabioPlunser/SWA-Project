package at.ac.uibk.swa.service.admin_deck_service;

import at.ac.uibk.swa.models.Deck;
import at.ac.uibk.swa.models.Permission;
import at.ac.uibk.swa.models.Person;
import at.ac.uibk.swa.service.AdminDeckService;
import at.ac.uibk.swa.service.PersonService;
import at.ac.uibk.swa.service.UserDeckService;
import at.ac.uibk.swa.util.StringGenerator;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
public class AdminDeckServiceGeneral {
    @Autowired
    AdminDeckService adminDeckService;
    @Autowired
    UserDeckService userDeckService;
    @Autowired
    PersonService personService;

    private Deck createDeck(String name, String creatorName) {
        Person creator = new Person(creatorName, StringGenerator.email(), StringGenerator.password(), Set.of(Permission.USER));
        assertTrue(personService.create(creator), "Unable to create user");
        Deck deck = new Deck(StringGenerator.deckDescription(), StringGenerator.deckDescription(), creator);
        assertTrue(userDeckService.create(deck), "Unable to create deck");
        return deck;
    }

    @Test
    public void testGetDeckByIdAsAdmin() {
        // given: a deck in the repository
        Deck deck = createDeck("deck-testGetDeckByIdAsAdmin", "person-testGetDeckByIdAsAdmin");
        UUID id = deck.getDeckId();

        // when: trying to load that deck by id from the repository
        Optional<Deck> maybeDeck = adminDeckService.findById(id);

        // then: retrieved deck must be correct
        assertTrue(maybeDeck.isPresent(), "Unable to find deck");
        assertEquals(deck, maybeDeck.get(), "Got deck " + maybeDeck.get() + " when deck " + deck + " was expected");
    }

    @Test
    public void testBlockDeck() {
        // given: a deck in the repository
        Deck deck = createDeck("deck-testBlockDeck", "person-testBlockDeck");
        UUID id = deck.getDeckId();

        // when: blocking that deck
        assertTrue(adminDeckService.block(deck), "Unable to block deck");

        // then: deck must be set as blocked within the repository
        Optional<Deck> maybeDeck = adminDeckService.findById(id);
        assertTrue(maybeDeck.isPresent(), "Unable to find deck");
        assertTrue(maybeDeck.get().isBlocked(), "Deck was not blocked");
    }

    @Test
    public void testUnblockDeck() {
        // given: a deck in the repository, that was blocked
        Deck deck = createDeck("deck-testUnblockDeck", "person-testUnblockDeck");
        UUID id = deck.getDeckId();
        assertTrue(adminDeckService.block(deck), "Unable to block deck");

        // when: unblocking that deck
        assertTrue(adminDeckService.unblock(deck), "Unable to unblock deck");

        // then: deck must be set as unblocked within the repository
        Optional<Deck> maybeDeck = adminDeckService.findById(id);
        assertTrue(maybeDeck.isPresent(), "Unable to find deck");
        assertFalse(maybeDeck.get().isBlocked(), "Deck was not blocked");
    }

    @Test
    public void testFindAllDecksAsAdmin() {
        // given: a number of decks in the repository, where one was deleted, one was blocked and one was published
        int numberOfDecks = 10;
        List<Deck> decks = new ArrayList<>();
        for (int i = 0; i < numberOfDecks; i++) {
            decks.add(createDeck("test-testFindAllDecksAsAdmin-"+(i+1), "person-testFindAllDecksAsAdmin-"+(i+1)));
        }
        assertTrue(userDeckService.delete(decks.get(0)));
        Deck deletedDeck = decks.remove(0);
        assertTrue(userDeckService.publish(decks.get(0)));
        assertTrue(adminDeckService.block(decks.get(1)));

        // when: loading all decks within the repository
        List<Deck> loadedDecks = adminDeckService.findAll();

        // then: all decks except the deleted one must be found
        for (Deck deck : decks) {
            assertTrue(loadedDecks.contains(deck), "Unable to find deck " + deck);
        }
        assertFalse(loadedDecks.contains(deletedDeck), "Found deleted deck");
    }
}
