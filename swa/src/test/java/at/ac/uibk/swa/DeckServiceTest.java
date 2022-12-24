package at.ac.uibk.swa;

import at.ac.uibk.swa.models.Deck;
import at.ac.uibk.swa.models.Permission;
import at.ac.uibk.swa.models.Person;
import at.ac.uibk.swa.service.CardService;
import at.ac.uibk.swa.service.DeckService;
import at.ac.uibk.swa.service.PersonService;
import at.ac.uibk.swa.util.StringGenerator;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
public class DeckServiceTest {
    @Autowired
    private DeckService deckService;
    @Autowired
    private CardService cardService;
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
                assertTrue(deckService.save(deck), "Could not save deck");
                savedDecks.put(creator, deck);
            }
        }

        // when: loading all decks from database
        List<Deck> loadedDecks = deckService.getAllDecks();

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
    public void testGetAllDecksAsUserCreatedUnpublished() {
        // given: a user that has created a deck and not published it
        Person person = new Person(
                "person-testGetAllDecksAsUser-main",
                StringGenerator.email(),
                StringGenerator.password(),
                Set.of(Permission.USER)
        );
        assertTrue(personService.save(person), "Unable to save user");

        String deckDescription = "Description";
        Deck deck = new Deck("deck-TestGetAllDecksAsUserCreatedUnpublished", deckDescription, person);
        assertTrue(deckService.save(deck));

        // when: loading all decks for that user
        List<Deck> decks = deckService.getAllDecks(person.getPersonId());

        // then: the user should be able to see that deck (and only that) without a change to its description
        assertTrue(decks.contains(deck));
        assertEquals(1, decks.size());
        assertEquals(deckDescription, decks.get(decks.indexOf(deck)).getDescription());
    }

    @Test
    public void testGetAllDecksAsAdmin() {
        
    }
}
