package at.ac.uibk.swa.service.user_deck_service;

import at.ac.uibk.swa.models.Deck;
import at.ac.uibk.swa.models.Permission;
import at.ac.uibk.swa.models.Person;
import at.ac.uibk.swa.repositories.DeckRepository;
import at.ac.uibk.swa.service.AdminDeckService;
import at.ac.uibk.swa.service.PersonService;
import at.ac.uibk.swa.service.UserDeckService;
import at.ac.uibk.swa.util.StringGenerator;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
public class UserDeckServiceTestGetAll {
    @Autowired
    private DeckRepository deckRepository;
    @Autowired
    private UserDeckService userDeckService;
    @Autowired
    private AdminDeckService adminDeckService;
    @Autowired
    private PersonService personService;

    private Person createUser(String username) {
        Person person = new Person(username, StringGenerator.email(), StringGenerator.password(), Set.of(Permission.USER));
        assertTrue(personService.create(person), "Unable to create user");
        return person;
    }

    @Test
    public void testGetAllDecksCreatedUnpublished() {
        // given: a user that has created a deck and not published it
        Person person = createUser("person-testGetAllDecksCreatedUnpublished");
        String deckDescription = "Description";
        Deck deck = new Deck("deck-testGetAllDecksCreatedUnpublished", deckDescription, person);
        assertTrue(userDeckService.create(deck), "Unable to create deck");

        // when: loading all decks for that user
        List<Deck> decks = userDeckService.getAllSavedDecks(person);

        // then: the user should be able to see that deck (and only that) without a change to its description
        assertTrue(decks.contains(deck), "Unable to find deck");
        assertEquals(1, decks.size(), "Found more decks than expected");
        assertEquals(deckDescription, decks.get(decks.indexOf(deck)).getDescription(), "Description has changed");
    }

    @Test
    public void testGetAllDecksCreatedPublished() {
        // given: a user that has created a deck and published it
        Person person = createUser("person-testGetAllDecksCreatedPublished");
        String deckDescription = "Description";
        Deck deck = new Deck("deck-testGetAllDecksCreatedPublished", deckDescription, person);
        assertTrue(userDeckService.create(deck), "Unable to create deck");
        assertTrue(userDeckService.publish(deck), "Unable to publish deck");

        // when: loading all decks for that user
        List<Deck> decks = userDeckService.getAllSavedDecks(person);

        // then: the user should be able to see that deck (and only that) without a change to its description
        assertTrue(decks.contains(deck), "Unable to find deck");
        assertEquals(1, decks.size(), "Found more decks than expected");
        assertEquals(deckDescription, decks.get(decks.indexOf(deck)).getDescription(), "Description has changed");
    }

    @Test
    public void testGetAllDecksCreatedBlocked() {
        // given: a user that has created a deck, but the deck has been blocked
        Person person = createUser("person-testGetAllDecksCreatedBlocked");
        String deckDescription = "Description";
        Deck deck = new Deck("deck-testGetAllDecksCreatedBlocked", deckDescription, person);
        assertTrue(userDeckService.create(deck), "Unable to create deck");
        assertTrue(adminDeckService.block(deck), "Unable to block deck");

        // when: loading all decks for that user
        List<Deck> decks = userDeckService.getAllSavedDecks(person);

        // then: the user should be able to see that deck (and only that), but the description should be changed
        // and contain info on the blocking
        assertTrue(decks.contains(deck), "Unable to find deck");
        assertEquals(1, decks.size(), "Found more decks than expected");
        assertNotEquals(deckDescription, decks.get(decks.indexOf(deck)).getDescription(), "Description has not changed");
        assertTrue(decks.get(decks.indexOf(deck)).getDescription().contains("blocked"), "Missing info on blocking");
    }

    @Test
    public void testGetAllDecksCreatedDeleted() {
        // given: a user that has created a deck, but then the deck has been deleted (only possible by the user)
        Person person = createUser("person-testGetAllDecksCreatedDeleted");
        String deckDescription = "Description";
        Deck deck = new Deck("deck-testGetAllDecksCreatedDeleted", deckDescription, person);
        assertTrue(userDeckService.create(deck), "Unable to create deck");
        assertTrue(userDeckService.delete(deck), "Unable to delete deck");

        // when: loading all decks for that user
        List<Deck> decks = userDeckService.getAllSavedDecks(person);

        // then: the user should not be able to see that deck
        assertEquals(0, decks.size(), "Found more decks than expected");
    }

    @Test
    public void testGetAllDecksSubscribedUnpublished() {
        // given: a published deck from another user, to which a user has subscribed and afterwards the creator
        // unpublished the deck
        Person person = createUser("person-testGetAllDecksSubscribedUnpublished");
        Person otherPerson = createUser("person-testGetAllDecksSubscribedUnpublished-other");
        String deckDescription = "Description";
        Deck deck = new Deck("deck-testGetAllDecksSubscribedUnpublished", deckDescription, otherPerson);
        assertTrue(userDeckService.create(deck), "Unable to create deck");
        assertTrue(userDeckService.publish(deck), "Unable to publish deck");
        assertTrue(userDeckService.subscribe(deck, person), "Unable to subscribe to deck");
        assertTrue(userDeckService.unpublish(deck), "Unable to unpublish deck");

        // when: loading all decks for the user
        List<Deck> decks = userDeckService.getAllSavedDecks(person);

        // then: the user should be able to see the deck (and only that), but the description should be changed and
        // contain info on unpublishing
        assertTrue(decks.contains(deck), "Unable to find deck");
        assertEquals(1, decks.size(), "Found more decks than expected");
        assertNotEquals(deckDescription, decks.get(decks.indexOf(deck)).getDescription(), "Description has not changed");
        assertTrue(decks.get(decks.indexOf(deck)).getDescription().contains("unpublished"), "Missing info on unpublishing");
    }

    @Test
    public void testGetAllDecksSubscribedPublished() {
        // given: a user and another user that has created a deck and published it, when the user subscribed to it
        Person person = createUser("person-testGetAllDecksSubscribedPublished");
        Person otherPerson = createUser("person-testGetAllDecksSubscribedPublished-other");
        String deckDescription = "Description";
        Deck deck = new Deck("deck-testGetAllDecksSubscribedPublished", deckDescription, otherPerson);
        assertTrue(userDeckService.create(deck), "Unable to create deck");
        assertTrue(userDeckService.publish(deck), "Unable to publish deck");
        assertTrue(userDeckService.subscribe(deck, person), "Unable to subscribe to deck");

        // when: loading all decks for the user
        List<Deck> decks = userDeckService.getAllSavedDecks(person);

        // then: the user should be able to see that deck (and only that) without a change to its description
        assertTrue(decks.contains(deck), "Unable to find deck");
        assertEquals(1, decks.size(), "Found more decks than expected");
        assertEquals(deckDescription, decks.get(decks.indexOf(deck)).getDescription(), "Description has changed");
    }

    @Test
    public void testGetAllDecksSubscribedBlocked() {
        // given: a user and another user that has created a deck and published it, when the user subscribed to it
        // and afterwards the deck has been blocked
        Person person = createUser("person-testGetAllDecksSubscribedBlocked");
        Person otherPerson = createUser("person-testGetAllDecksSubscribedBlocked-other");
        String deckDescription = "Description";
        Deck deck = new Deck("deck-testGetAllDecksSubscribedBlocked", deckDescription, otherPerson);
        assertTrue(userDeckService.create(deck), "Unable to create deck");
        assertTrue(userDeckService.publish(deck), "Unable to publish deck");
        assertTrue(userDeckService.subscribe(deck, person), "Unable to subscribe to deck");
        assertTrue(adminDeckService.block(deck), "Unable to block deck");

        // when: loading all decks for the user
        List<Deck> decks = userDeckService.getAllSavedDecks(person);

        // then: the user should be able to see that deck (and only that), but the description should be changed and
        // contain info on blocking
        assertTrue(decks.contains(deck), "Unable to find deck");
        assertEquals(1, decks.size(), "Found more decks than expected");
        assertNotEquals(deckDescription, decks.get(decks.indexOf(deck)).getDescription(), "Description has not changed");
        assertTrue(decks.get(decks.indexOf(deck)).getDescription().contains("blocked"), "Missing info on blocking");
    }

    @Test
    public void testGetAllDecksSubscribedDeleted() {
        // given: a user and another user that has created a deck and published it, when the user subscribed to it
        // and afterwards the deck has been deleted
        Person person = createUser("person-testGetAllDecksSubscribedDeleted");
        Person otherPerson = createUser("person-testGetAllDecksSubscribedDeleted-other");
        String deckDescription = "Description";
        Deck deck = new Deck("deck-testGetAllDecksSubscribedDeleted", deckDescription, otherPerson);
        assertTrue(userDeckService.create(deck), "Unable to create deck");
        assertTrue(userDeckService.publish(deck), "Unable to publish deck");
        assertTrue(userDeckService.subscribe(deck, person), "Unable to subscribe to deck");
        assertTrue(userDeckService.delete(deck), "Unable to delete deck");

        // when: loading all decks for the user
        List<Deck> decks = userDeckService.getAllSavedDecks(person);

        // then: the user should be able to see that deck (and only that), but the description should be changed and
        // contain info on deleting
        assertTrue(decks.contains(deck), "Unable to find deck");
        assertEquals(1, decks.size(), "Found more decks than expected");
        assertNotEquals(deckDescription, decks.get(decks.indexOf(deck)).getDescription(), "Description has not changed");
        assertTrue(decks.get(decks.indexOf(deck)).getDescription().contains("deleted"), "Missing info on deleting");
    }
}
