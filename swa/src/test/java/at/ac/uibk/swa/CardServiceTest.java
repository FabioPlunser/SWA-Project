package at.ac.uibk.swa;

import at.ac.uibk.swa.models.Card;
import at.ac.uibk.swa.models.Deck;
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
public class CardServiceTest {
    @Autowired
    private DeckService deckService;
    @Autowired
    private CardService cardService;
    @Autowired
    private PersonService personService;

    @Test
    public void testSaveAndGetCards() {
        // given: demo creators, decks and cards saved to database
        int numberOfCreators = 5;
        int numberOfDecksPerCreator = 5;
        int numberOfCardsPerDeck = 10;

        List<Person> creators = new ArrayList<>();
        Map<Person, Deck> decks = new HashMap<>();
        Map<Deck, Card> savedCards = new HashMap<>();

        for (int i = 0; i < numberOfCreators; i++) {
            Person creator = new Person(
                    "person-TestSaveAndGetCards-" + (i+1),
                    StringGenerator.email(),
                    StringGenerator.password(),
                    Set.of()
            );
            assertTrue(personService.save(creator), "Could not save user");
            creators.add(creator);
            for (int j = 0; j < numberOfDecksPerCreator; j++) {
                Deck deck = new Deck(
                        "deck-TestSaveAndGetCards-" + (j+1),
                        StringGenerator.deckDescription(),
                        creator
                );
                assertTrue(deckService.save(deck), "Could not save deck");
                decks.put(creator, deck);
                for (int k = 0; k < numberOfCardsPerDeck; k++) {
                    Card card = new Card(
                            StringGenerator.cardText(),
                            StringGenerator.cardText(),
                            false,
                            deck
                    );
                    assertTrue(cardService.save(card), "Could not save card");
                    savedCards.put(deck, card);
                }
            }
        }
    }
}
