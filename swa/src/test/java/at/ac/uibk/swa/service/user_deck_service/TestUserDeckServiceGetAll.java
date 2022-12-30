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
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
public class TestUserDeckServiceGetAll {
    @Autowired
    private DeckRepository deckRepository;
    @Autowired
    private UserDeckService userDeckService;
    @Autowired
    private AdminDeckService adminDeckService;
    @Autowired
    private PersonService personService;

    private Person createUserAndLogin() {
        String password = StringGenerator.password();
        Person person = new Person(StringGenerator.username(), StringGenerator.email(), password, Set.of(Permission.USER));
        assertTrue(personService.create(person), "Unable to create user");
        return (Person) MockAuthContext.setLoggedInUser(person);
    }

    @AfterEach
    public void logout() {
        personService.logout();
    }

    @Test
    public void testGetAllDecksCreatedUnpublished() {
        // given: a user that has created a deck and not published it
        Person person = createUserAndLogin();
        String deckDescription = StringGenerator.deckDescription();
        Deck deck = new Deck(StringGenerator.deckName(), deckDescription);
        assertTrue(userDeckService.create(deck), "Unable to create deck");

        // when: loading all decks for that user
        Optional<List<Deck>> maybeDecks = userDeckService.getAllSavedDecks();

        // then: the user should be able to see that deck (and only that) without a change to its description
        assertTrue(maybeDecks.isPresent(), "Unable to load decks");
        List<Deck> decks = maybeDecks.get();
        assertTrue(decks.contains(deck), "Unable to find deck");
        assertEquals(1, decks.size(), "Found more decks than expected");
        assertEquals(deckDescription, decks.get(decks.indexOf(deck)).getDescription(), "Description has changed");
    }

    @Test
    public void testGetAllDecksCreatedPublished() {
        // given: a user that has created a deck and published it
        Person person = createUserAndLogin();
        String deckDescription = StringGenerator.deckDescription();
        Deck deck = new Deck(StringGenerator.deckName(), deckDescription);
        assertTrue(userDeckService.create(deck), "Unable to create deck");
        assertTrue(userDeckService.publish(deck.getDeckId()), "Unable to publish deck");

        // when: loading all decks for that user
        Optional<List<Deck>> maybeDecks = userDeckService.getAllSavedDecks();

        // then: the user should be able to see that deck (and only that) without a change to its description
        assertTrue(maybeDecks.isPresent(), "Unable to load decks");
        List<Deck> decks = maybeDecks.get();
        assertTrue(decks.contains(deck), "Unable to find deck");
        assertEquals(1, decks.size(), "Found more decks than expected");
        assertEquals(deckDescription, decks.get(decks.indexOf(deck)).getDescription(), "Description has changed");
    }

    @Test
    public void testGetAllDecksCreatedBlocked() {
        // given: a user that has created a deck, but the deck has been blocked
        Person person = createUserAndLogin();
        String deckDescription = StringGenerator.deckDescription();
        Deck deck = new Deck(StringGenerator.deckName(), deckDescription);
        assertTrue(userDeckService.create(deck), "Unable to create deck");
        assertTrue(adminDeckService.block(deck), "Unable to block deck");

        // when: loading all decks for that user
        Optional<List<Deck>> maybeDecks = userDeckService.getAllSavedDecks();

        // then: the user should be able to see that deck (and only that), but the description should be changed
        // and contain info on the blocking
        assertTrue(maybeDecks.isPresent(), "Unable to load decks");
        List<Deck> decks = maybeDecks.get();
        assertTrue(decks.contains(deck), "Unable to find deck");
        assertEquals(1, decks.size(), "Found more decks than expected");
        assertNotEquals(deckDescription, decks.get(decks.indexOf(deck)).getDescription(), "Description has not changed");
        assertTrue(decks.get(decks.indexOf(deck)).getDescription().contains("blocked"), "Missing info on blocking");
    }

    @Test
    public void testGetAllDecksCreatedDeleted() {
        // given: a user that has created a deck, but then the deck has been deleted (only possible by the user)
        Person person = createUserAndLogin();
        String deckDescription = StringGenerator.deckDescription();
        Deck deck = new Deck(StringGenerator.deckName(), deckDescription);
        assertTrue(userDeckService.create(deck), "Unable to create deck");
        assertTrue(userDeckService.delete(deck.getDeckId()), "Unable to delete deck");

        // when: loading all decks for that user
        Optional<List<Deck>> maybeDecks = userDeckService.getAllSavedDecks();

        // then: the user should not be able to see that deck
        assertTrue(maybeDecks.isPresent(), "Unable to load decks");
        List<Deck> decks = maybeDecks.get();
        assertEquals(0, decks.size(), "Found more decks than expected");
    }

    @Test
    public void testGetAllDecksSubscribedUnpublished() {
        // given: a published deck from a creator, to which a user has subscribed and afterwards the creator
        // unpublished the deck
        Person creator = createUserAndLogin();
        String deckDescription = StringGenerator.deckDescription();
        Deck deck = new Deck(StringGenerator.deckName(), deckDescription);
        assertTrue(userDeckService.create(deck), "Unable to create deck");
        assertTrue(userDeckService.publish(deck.getDeckId()), "Unable to publish deck");
        Person person = createUserAndLogin();
        assertTrue(userDeckService.subscribe(deck.getDeckId()), "Unable to subscribe to deck");
        MockAuthContext.setLoggedInUser(creator);
        assertTrue(userDeckService.unpublish(deck.getDeckId()), "Unable to unpublish deck");
        // workaround, as mocking does currently not allow for reloading the deck from the repository
        person.getSavedDecks().get(0).setPublished(false);
        MockAuthContext.setLoggedInUser(person);

        // when: loading all decks for the user
        Optional<List<Deck>> maybeDecks = userDeckService.getAllSavedDecks();

        // then: the user should be able to see the deck (and only that), but the description should be changed and
        // contain info on unpublishing
        assertTrue(maybeDecks.isPresent(), "Unable to load decks");
        List<Deck> decks = maybeDecks.get();
        assertTrue(decks.contains(deck), "Unable to find deck");
        assertEquals(1, decks.size(), "Found more decks than expected");
        assertNotEquals(deckDescription, decks.get(decks.indexOf(deck)).getDescription(), "Description has not changed");
        assertTrue(decks.get(decks.indexOf(deck)).getDescription().contains("unpublished"), "Missing info on unpublishing");
    }

    @Test
    public void testGetAllDecksSubscribedPublished() {
        // given: a user and another user that has created a deck and published it, when the user subscribed to it
        Person creator = createUserAndLogin();
        String deckDescription = StringGenerator.deckDescription();
        Deck deck = new Deck(StringGenerator.deckName(), deckDescription);
        assertTrue(userDeckService.create(deck), "Unable to create deck");
        assertTrue(userDeckService.publish(deck.getDeckId()), "Unable to publish deck");
        Person person = createUserAndLogin();
        assertTrue(userDeckService.subscribe(deck.getDeckId()), "Unable to subscribe to deck");

        // when: loading all decks for the user
        Optional<List<Deck>> maybeDecks = userDeckService.getAllSavedDecks();

        // then: the user should be able to see that deck (and only that) without a change to its description
        assertTrue(maybeDecks.isPresent(), "Unable to load decks");
        List<Deck> decks = maybeDecks.get();
        assertTrue(decks.contains(deck), "Unable to find deck");
        assertEquals(1, decks.size(), "Found more decks than expected");
        assertEquals(deckDescription, decks.get(decks.indexOf(deck)).getDescription(), "Description has changed");
    }

    @Test
    public void testGetAllDecksSubscribedBlocked() {
        // given: a user and another user that has created a deck and published it, when the user subscribed to it
        // and afterwards the deck has been blocked
        Person creator = createUserAndLogin();
        String deckDescription = StringGenerator.deckDescription();
        Deck deck = new Deck(StringGenerator.deckName(), deckDescription);
        assertTrue(userDeckService.create(deck), "Unable to create deck");
        assertTrue(userDeckService.publish(deck.getDeckId()), "Unable to publish deck");
        Person person = createUserAndLogin();
        assertTrue(userDeckService.subscribe(deck.getDeckId()), "Unable to subscribe to deck");
        assertTrue(adminDeckService.block(deck), "Unable to block deck");
        // workaround, as mocking does currently not allow for reloading the deck from the repository
        person.getSavedDecks().get(0).setBlocked(true);

        // when: loading all decks for the user
        Optional<List<Deck>> maybeDecks = userDeckService.getAllSavedDecks();

        // then: the user should be able to see that deck (and only that), but the description should be changed and
        // contain info on blocking
        assertTrue(maybeDecks.isPresent(), "Unable to load decks");
        List<Deck> decks = maybeDecks.get();
        assertTrue(decks.contains(deck), "Unable to find deck");
        assertEquals(1, decks.size(), "Found more decks than expected");
        assertNotEquals(deckDescription, decks.get(decks.indexOf(deck)).getDescription(), "Description has not changed");
        assertTrue(decks.get(decks.indexOf(deck)).getDescription().contains("blocked"), "Missing info on blocking");
    }

    @Test
    public void testGetAllDecksSubscribedDeleted() {
        // given: a user and another user that has created a deck and published it, when the user subscribed to it
        // and afterwards the deck has been deleted
        Person creator = createUserAndLogin();
        String deckDescription = StringGenerator.deckDescription();
        Deck deck = new Deck(StringGenerator.deckName(), deckDescription);
        assertTrue(userDeckService.create(deck), "Unable to create deck");
        assertTrue(userDeckService.publish(deck.getDeckId()), "Unable to publish deck");
        Person person = createUserAndLogin();
        assertTrue(userDeckService.subscribe(deck.getDeckId()), "Unable to subscribe to deck");
        MockAuthContext.setLoggedInUser(creator);
        assertTrue(userDeckService.delete(deck.getDeckId()), "Unable to delete deck");
        // workaround, as mocking does currently not allow for reloading the deck from the repository
        person.getSavedDecks().get(0).setDeleted(true);
        MockAuthContext.setLoggedInUser(person);

        // when: loading all decks for the user
        Optional<List<Deck>> maybeDecks = userDeckService.getAllSavedDecks();

        // then: the user should be able to see that deck (and only that), but the description should be changed and
        // contain info on deleting
        assertTrue(maybeDecks.isPresent(), "Unable to load decks");
        List<Deck> decks = maybeDecks.get();
        assertTrue(decks.contains(deck), "Unable to find deck");
        assertEquals(1, decks.size(), "Found more decks than expected");
        assertNotEquals(deckDescription, decks.get(decks.indexOf(deck)).getDescription(), "Description has not changed");
        assertTrue(decks.get(decks.indexOf(deck)).getDescription().contains("deleted"), "Missing info on deleting");
    }
}
