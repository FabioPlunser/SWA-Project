package at.ac.uibk.swa;

import at.ac.uibk.swa.models.Deck;
import at.ac.uibk.swa.models.Permission;
import at.ac.uibk.swa.models.Person;
import at.ac.uibk.swa.repositories.DeckRepository;
import at.ac.uibk.swa.service.AdminDeckService;
import at.ac.uibk.swa.service.CardService;
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
public class UserDeckServiceTest {
    @Autowired
    private DeckRepository deckRepository;
    @Autowired
    private UserDeckService userDeckService;
    @Autowired
    private AdminDeckService adminDeckService;
    @Autowired
    private PersonService personService;

    @Test
    public void testSaveAndGetDecks() {
        // given: demo creators and decks saved to database
        int numberOfCreators = 5;
        int numberOfDecksPerCreator = 10;
        List<Person> creators = new ArrayList<>();
        Map<Person, Deck> savedDecks = new HashMap<>();
        for (int i = 0; i < numberOfCreators; i++) {
            Person creator = new Person(
                    "person-TestSaveAndGetDecks-" + (i+1),
                    StringGenerator.email(),
                    StringGenerator.password(),
                    Set.of()
            );
            assertTrue(personService.save(creator), "Could not save user");
            creators.add(creator);
            for (int j = 0; j < numberOfDecksPerCreator; j++) {
                Deck deck = new Deck(
                        "deck-TestSaveAndGetDecks-" + (j+1),
                        StringGenerator.deckDescription(),
                        creator
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
    public void testGetAllDecksCreatedUnpublished() {
        // given: a user that has created a deck and not published it
        Person person = new Person(
                "person-testGetAllDecksCreatedUnpublished",
                StringGenerator.email(),
                StringGenerator.password(),
                Set.of(Permission.USER)
        );
        assertTrue(personService.save(person), "Unable to save user");

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
        Person person = new Person(
                "person-testGetAllDecksCreatedPublished",
                StringGenerator.email(),
                StringGenerator.password(),
                Set.of(Permission.USER)
        );
        assertTrue(personService.save(person), "Unable to save user");

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
        Person person = new Person(
                "person-testGetAllDecksCreatedBlocked",
                StringGenerator.email(),
                StringGenerator.password(),
                Set.of(Permission.USER)
        );
        assertTrue(personService.save(person), "Unable to save user");

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
        Person person = new Person(
                "person-testGetAllDecksCreatedDeleted",
                StringGenerator.email(),
                StringGenerator.password(),
                Set.of(Permission.USER)
        );
        assertTrue(personService.save(person), "Unable to save user");

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
        Person person = new Person(
                "person-testGetAllDecksSubscribedUnpublished",
                StringGenerator.email(),
                StringGenerator.password(),
                Set.of(Permission.USER)
        );
        assertTrue(personService.save(person), "Unable to save user");
        Person otherPerson = new Person(
                "person-testGetAllDecksSubscribedUnpublished-other",
                StringGenerator.email(),
                StringGenerator.password(),
                Set.of(Permission.USER)
        );
        assertTrue(personService.save(otherPerson), "Unable to save user");

        String deckDescription = "Description";
        Deck deck = new Deck("deck-testGetAllDecksSubscribedUnpublished", deckDescription, otherPerson);
        assertTrue(userDeckService.create(deck), "Unable to create deck");
        assertTrue(userDeckService.publish(deck), "Unable to publish deck");
        assertTrue(userDeckService.subscribeToDeck(deck, person), "Unable to subscribe to deck");
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
        Person person = new Person(
                "person-testGetAllDecksSubscribedPublished",
                StringGenerator.email(),
                StringGenerator.password(),
                Set.of(Permission.USER)
        );
        assertTrue(personService.save(person), "Unable to save user");
        Person otherPerson = new Person(
                "person-testGetAllDecksSubscribedPublished-other",
                StringGenerator.email(),
                StringGenerator.password(),
                Set.of(Permission.USER)
        );
        assertTrue(personService.save(otherPerson), "Unable to save user");

        String deckDescription = "Description";
        Deck deck = new Deck("deck-testGetAllDecksSubscribedPublished", deckDescription, otherPerson);
        assertTrue(userDeckService.create(deck), "Unable to create deck");
        assertTrue(userDeckService.publish(deck), "Unable to publish deck");
        assertTrue(userDeckService.subscribeToDeck(deck, person), "Unable to subscribe to deck");

        // when: loading all decks for the user
        List<Deck> decks = userDeckService.getAllSavedDecks(person);

        // then: the user should be able to see that deck (and only that) without a change to its description
        assertTrue(decks.contains(deck), "Unable to find deck");
        assertEquals(1, decks.size(), "Found more decks than expected");
        assertEquals(deckDescription, decks.get(decks.indexOf(deck)).getDescription(), "Description has changed");
    }
}
