package at.ac.uibk.swa.service.user_deck_service;

import at.ac.uibk.swa.models.Deck;
import at.ac.uibk.swa.models.Permission;
import at.ac.uibk.swa.models.Person;
import at.ac.uibk.swa.repositories.DeckRepository;
import at.ac.uibk.swa.service.AdminDeckService;
import at.ac.uibk.swa.service.PersonService;
import at.ac.uibk.swa.service.UserDeckService;
import at.ac.uibk.swa.util.MockAuthContext;
import at.ac.uibk.swa.util.StringGenerator;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
public class UserDeckServiceTestGetOwned {
    @Autowired
    private DeckRepository deckRepository;
    @Autowired
    private UserDeckService userDeckService;
    @Autowired
    private AdminDeckService adminDeckService;
    @Autowired
    private PersonService personService;

    private Person createUserAndLogin(String username) {
        Person person = new Person(username, StringGenerator.email(), StringGenerator.password(), Set.of(Permission.USER));
        assertTrue(personService.create(person), "Unable to create user");
        return (Person) MockAuthContext.setLoggedInUser(person);
    }

    @Test
    public void testGetAllOwnedDecksUnpublished() {
        // given: a user that created a number of decks
        int numberOfDecks = 10;
        List<Deck> createdDecks = new ArrayList<>();
        Person creator = createUserAndLogin("person-testGetAllOwnedDecksUnpublished");
        for (int i = 0; i < numberOfDecks; i++) {
            Deck deck = new Deck("deck-testGetAllOwnedDecksUnpublished-" + (i+1), StringGenerator.deckDescription());
            assertTrue(userDeckService.create(deck), "Unable to create deck");
            createdDecks.add(deck);
        }

        // when: loading all owned decks from the repository
        Optional<List<Deck>> maybeOwnedDecks = userDeckService.getAllOwnedDecks();

        // then: all decks must be included
        assertTrue(maybeOwnedDecks.isPresent(), "Decks could not be loaded");
        List<Deck> ownedDecks = maybeOwnedDecks.get();
        assertEquals(createdDecks.size(), ownedDecks.size(), "Found more/less decks than expected");
        for (Deck createdDeck : createdDecks) {
            assertTrue(ownedDecks.contains(createdDeck), "Unable to find deck " + createdDeck);
        }
    }

    @Test
    public void testGetAllOwnedDecksPublished() {
        // given: a user that created a number of decks of which 1 has been published
        int numberOfDecks = 10;
        List<Deck> createdDecks = new ArrayList<>();
        Person creator = createUserAndLogin("person-testGetAllOwnedDecksPublished");
        for (int i = 0; i < numberOfDecks; i++) {
            Deck deck = new Deck("deck-testGetAllOwnedDecksPublished-" + (i+1), StringGenerator.deckDescription());
            assertTrue(userDeckService.create(deck), "Unable to create deck");
            createdDecks.add(deck);
        }
        assertTrue(userDeckService.publish(createdDecks.get(0)), "Unable to publish deck");

        // when: loading all owned decks from the repository
        Optional<List<Deck>> maybeOwnedDecks = userDeckService.getAllOwnedDecks();

        // then: all decks must be included
        assertTrue(maybeOwnedDecks.isPresent(), "Decks could not be loaded");
        List<Deck> ownedDecks = maybeOwnedDecks.get();
        assertEquals(createdDecks.size(), ownedDecks.size(), "Found more/less decks than expected");
        for (Deck createdDeck : createdDecks) {
            assertTrue(ownedDecks.contains(createdDeck), "Unable to find deck " + createdDeck);
        }
    }

    @Test
    public void testGetAllOwnedDecksBlocked() {
        // given: a user that created a number of decks of which 1 has been blocked
        int numberOfDecks = 10;
        List<Deck> createdDecks = new ArrayList<>();
        Person creator = createUserAndLogin("person-testGetAllOwnedDecksBlocked");
        for (int i = 0; i < numberOfDecks; i++) {
            Deck deck = new Deck("deck-testGetAllOwnedDecksBlocked-" + (i+1), StringGenerator.deckDescription());
            assertTrue(userDeckService.create(deck), "Unable to create deck");
            createdDecks.add(deck);
        }
        assertTrue(adminDeckService.block(createdDecks.get(0)), "Unable to block deck");

        // when: loading all owned decks from the repository
        Optional<List<Deck>> maybeOwnedDecks = userDeckService.getAllOwnedDecks();

        // then: all decks must be included, but description should be changed on blocked deck
        assertTrue(maybeOwnedDecks.isPresent(), "Decks could not be loaded");
        List<Deck> ownedDecks = maybeOwnedDecks.get();
        assertEquals(createdDecks.size(), ownedDecks.size(), "Found more/less decks than expected");
        for (Deck createdDeck : createdDecks) {
            assertTrue(ownedDecks.contains(createdDeck), "Unable to find deck " + createdDeck);
            if (createdDeck.isBlocked()) {
                assertTrue(ownedDecks.get(ownedDecks.indexOf(createdDeck)).getDescription().contains("blocked"), "Missing info on blocking");
            }
        }
    }

    @Test
    public void testGetAllOwnedDecksDeleted() {
        // given: a user that created a number of decks of which 1 has been deleted
        int numberOfDecks = 10;
        List<Deck> createdDecks = new ArrayList<>();
        Person creator = createUserAndLogin("person-testGetAllOwnedDecksDeleted");
        for (int i = 0; i < numberOfDecks; i++) {
            Deck deck = new Deck("deck-testGetAllOwnedDecksDeleted-" + (i+1), StringGenerator.deckDescription());
            assertTrue(userDeckService.create(deck), "Unable to create deck");
            createdDecks.add(deck);
        }
        assertTrue(userDeckService.delete(createdDecks.get(0).getDeckId()), "Unable to delete deck");

        // when: loading all owned decks from the repository
        Optional<List<Deck>> maybeOwnedDecks = userDeckService.getAllOwnedDecks();

        // then: all but the deleted deck must be included
        assertTrue(maybeOwnedDecks.isPresent(), "Decks could not be loaded");
        List<Deck> ownedDecks = maybeOwnedDecks.get();
        assertEquals(createdDecks.size() - 1, ownedDecks.size(), "Found more/less decks than expected");
        for (Deck createdDeck : createdDecks) {
            if (createdDeck.isDeleted()) {
                assertFalse(ownedDecks.contains(createdDeck), "Found a deleted deck");
            } else {
                assertTrue(ownedDecks.contains(createdDeck), "Unable to find deck " + createdDeck);
            }
        }
    }
}
