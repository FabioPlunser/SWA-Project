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

import java.util.*;
import java.util.function.Predicate;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
public class UserDeckServiceTestGeneral {
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
    public void testSaveAndGetDecks() {
        // given: demo creators and decks saved to database
        int numberOfCreators = 5;
        int numberOfDecksPerCreator = 10;
        List<Person> creators = new ArrayList<>();
        Map<Person, Deck> savedDecks = new HashMap<>();
        for (int i = 0; i < numberOfCreators; i++) {
            Person creator = createUserAndLogin("person-TestSaveAndGetDecks-" + (i+1));
            creators.add(creator);
            for (int j = 0; j < numberOfDecksPerCreator; j++) {
                Deck deck = new Deck(
                        "deck-TestSaveAndGetDecks-" + (j+1),
                        StringGenerator.deckDescription()
                );
                assertTrue(userDeckService.create(deck), "Unable to create deck");
                savedDecks.put(creator, deck);
            }
        }

        // when: loading all decks from database
        List<Deck> loadedDecks = deckRepository.findAll();

        // then: all saved decks must be found again and attributes must be identical
        for (Map.Entry<Person,Deck> entry : savedDecks.entrySet()) {
            Person creator = entry.getKey();
            Deck deck = entry.getValue();
            assertTrue(loadedDecks.contains(deck), "Could not find deck " + deck);
            Deck loadedDeck = loadedDecks.get(loadedDecks.indexOf(deck));
            assertEquals(creator, loadedDeck.getCreator(), "Wrong creator of " + deck);
            assertEquals(deck.getName(), loadedDeck.getName(), "Wrong name of " + deck);
            assertEquals(deck.getDescription(), loadedDeck.getDescription(), "Wrong description of " + deck);
            assertFalse(loadedDeck.isBlocked(), deck + " has been blocked");
            assertFalse(loadedDeck.isDeleted(), deck + " has been deleted");
            assertFalse(loadedDeck.isPublished(), deck + " has been published");
        }
    }

    @Test
    public void testGetDeckById() {
        // given: a user in the database that created a deck
        Person creator = createUserAndLogin("person-testGetDeckById");
        Deck deck = new Deck("deck-testGetDeckById", StringGenerator.deckDescription());
        assertTrue(userDeckService.create(deck), "Unable to create deck");
        UUID id = deck.getDeckId();

        // when: trying to load that deck by id from the repository
        Optional<Deck> maybeDeck = userDeckService.findById(id);

        // then: retrieved deck must be correct
        assertTrue(maybeDeck.isPresent(), "Did not find deck by id");
        assertEquals(deck, maybeDeck.get(), "Got deck " + maybeDeck.get() + " when deck " + deck + " was expected");
    }

    @Test
    public void testUpdateDeck() {
        // given: a user that created a deck in the repository
        Person creator = createUserAndLogin("person-testUpdateDeck");
        Deck deck = new Deck("deck-testUpdateDeck", StringGenerator.deckDescription());
        assertTrue(userDeckService.create(deck), "Unable to create deck");

        // when: updating the deck
        String updatedName = "updated-name";
        String updatedDescription = "updated-description";
        assertTrue(userDeckService.update(deck.getDeckId(), updatedName, updatedDescription), "Unable to update deck");

        // then: the deck should still be available in the repository and new attributes should be set
        Optional<Deck> maybeDeck = userDeckService.findById(deck.getDeckId());
        assertTrue(maybeDeck.isPresent(), "Unable to find deck");
        Deck foundDeck = maybeDeck.get();
        assertEquals(updatedName, foundDeck.getName(), "Name has not been updated");
        assertEquals(updatedDescription, foundDeck.getDescription(), "Description has not been updated");
        assertEquals(creator, foundDeck.getCreator(), "Creator has changed");
        assertFalse(foundDeck.isPublished(), "Deck has been published");
        assertFalse(foundDeck.isBlocked(), "Deck has been blocked");
        assertFalse(foundDeck.isDeleted(), "Deck has been deleted");
    }

    @Test
    public void testUpdateDeckViaCreate() {
        // given: a user that created a deck in the repository
        Person creator = createUserAndLogin("person-testUpdateDeckViaCreate");
        String originalName = "deck-testUpdateDeckViaCreate";
        String originalDescription = StringGenerator.deckDescription();
        Deck deck = new Deck(originalName, originalDescription);
        assertTrue(userDeckService.create(deck), "Unable to create deck");
        UUID id = deck.getDeckId();

        // when: changing the deck by interfering with the model directly
        deck.setName("new");
        deck.setDescription("new");
        deck.setPublished(true);
        deck.setBlocked(true);
        deck.setDeleted(true);

        // then: saving the modification via create() should not be possible
        assertFalse(userDeckService.create(deck), "Changed deck got created again");
        Optional<Deck> maybeDeck = userDeckService.findById(id);
        assertTrue(maybeDeck.isPresent(), "Unable to find original deck in repository");
        Deck foundDeck = maybeDeck.get();
        assertEquals(originalName, foundDeck.getName(), "Name was changed");
        assertEquals(originalDescription, foundDeck.getDescription(), "Description was changed");
        assertEquals(creator, foundDeck.getCreator(), "Creator was changed");
        assertFalse(foundDeck.isPublished(), "Deck was published");
        assertFalse(foundDeck.isBlocked(), "Deck was blocked");
        assertFalse(foundDeck.isDeleted(), "Deck was deleted");
    }

    @Test
    public void testDeleteDeck() {
        // given: a user that created a deck in the database
        Person creator = createUserAndLogin("person-testDeleteDecK");
        Deck deck = new Deck("deck-testDeleteDeck", StringGenerator.deckDescription());
        assertTrue(userDeckService.create(deck), "Unable to create deck");
        UUID id = deck.getDeckId();

        // when: deleting that deck
        assertTrue(userDeckService.delete(deck.getDeckId()), "Unable to delete deck");

        // then: deck should be set to deleted
        Optional<Deck> maybeDeck = userDeckService.findById(id);
        assertTrue(maybeDeck.isPresent(), "Unable to find deck");
        assertTrue(maybeDeck.get().isDeleted(), "Deck was not deleted");
    }

    @Test
    public void testPublishDeck() {
        // given: a user that created a deck in the database
        Person creator = createUserAndLogin("person-testPublishDeck");
        Deck deck = new Deck("deck-testPublishDeck", StringGenerator.deckDescription());
        assertTrue(userDeckService.create(deck), "Unable to create deck");
        UUID id = deck.getDeckId();

        // when: publishing that deck
        assertTrue(userDeckService.publish(deck.getDeckId()), "Unable to publish deck");

        // then: deck should be set to published
        Optional<Deck> maybeDeck = userDeckService.findById(id);
        assertTrue(maybeDeck.isPresent(), "Unable to find deck");
        assertTrue(maybeDeck.get().isPublished(), "Deck was not published");
    }

    @Test
    public void testUnpublishDeck() {
        // given: a user that created a deck in the database and published it
        Person creator = createUserAndLogin("person-testUnpublishDeck");
        Deck deck = new Deck("deck-testUnpublishDeck", StringGenerator.deckDescription());
        assertTrue(userDeckService.create(deck), "Unable to create deck");
        assertTrue(userDeckService.publish(deck.getDeckId()), "Unable to publish deck");
        UUID id = deck.getDeckId();

        // when: unpublishing that deck
        assertTrue(userDeckService.unpublish(deck.getDeckId()), "Unable to unpublish deck");

        // then: deck should be set to published
        Optional<Deck> maybeDeck = userDeckService.findById(id);
        assertTrue(maybeDeck.isPresent(), "Unable to find deck");
        assertFalse(maybeDeck.get().isPublished(), "Deck was not unpublished");
    }

    @Test
    public void testFindAllAvailableDecks() {
        // given: a user in the database, where the user has created 4 decks:
        //  - published
        //  - unpublished
        //  - blocked
        //  - deleted
        Person person = createUserAndLogin("person-testFindAllAvailableDecks");
        Deck publishedDeck = new Deck("deck-findAllAvailableDecks-published", "published");
        assertTrue(userDeckService.create(publishedDeck), "Unable to create deck");
        assertTrue(userDeckService.publish(publishedDeck.getDeckId()), "Unable to publish deck");
        Deck unpublishedDeck = new Deck("deck-findAllAvailableDecks-published", "unpublished");
        assertTrue(userDeckService.create(unpublishedDeck), "Unable to create deck");
        Deck blockedDeck = new Deck("deck-findAllAvailableDecks-blocked", "blocked");
        assertTrue(userDeckService.create(blockedDeck), "Unable to create deck");
        assertTrue(adminDeckService.block(blockedDeck), "Unable to block deck");
        Deck deletedDeck = new Deck("deck-findAllAvailableDecks-deleted", "deleted");
        assertTrue(userDeckService.create(deletedDeck), "Unable to create deck");
        assertTrue(userDeckService.delete(deletedDeck.getDeckId()), "Unable to delete deck");
        MockAuthContext.setLoggedInUser(null);

        // when: searching for decks available for subscription
        List<Deck> availableDecks = userDeckService.findAllAvailableDecks();

        // then: only available, public decks (maybe also from other unittests) should be returned
        assertTrue(availableDecks.contains(publishedDeck), "Unable to find published deck");
        assertEquals(0, availableDecks.stream().filter(Predicate.not(Deck::isPublished)).count(), "Found unpublished decks");
        assertEquals(0, availableDecks.stream().filter(Deck::isBlocked).count(), "Found blocked decks");
        assertEquals(0, availableDecks.stream().filter(Deck::isDeleted).count(), "Found deleted decks");
    }
}
