package at.ac.uibk.swa.service.card_service;

import at.ac.uibk.swa.models.Card;
import at.ac.uibk.swa.models.Deck;
import at.ac.uibk.swa.models.Permission;
import at.ac.uibk.swa.models.Person;
import at.ac.uibk.swa.repositories.CardRepository;
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
public class CardServiceTestGeneral {
    @Autowired
    private UserDeckService userDeckService;
    @Autowired
    private AdminDeckService adminDeckService;
    @Autowired
    private CardService cardService;
    @Autowired
    private PersonService personService;
    @Autowired
    private CardRepository cardRepository;

    private Person createUser(String username) {
        Person person = new Person(username, StringGenerator.email(), StringGenerator.password(), Set.of(Permission.USER));
        assertTrue(personService.create(person), "Unable to create user");
        return person;
    }

    private Deck createDeck(String name, Person creator) {
        Deck deck = new Deck(name, StringGenerator.deckDescription(), creator);
        assertTrue(userDeckService.create(deck), "Unable to create deck");
        return deck;
    }

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
            Person creator = createUser("person-TestSaveAndGetCards-" + (i+1));
            creators.add(creator);
            for (int j = 0; j < numberOfDecksPerCreator; j++) {
                Deck deck = createDeck("deck-TestSaveAndGetCards-" + (j+1), creator);
                decks.put(creator, deck);
                for (int k = 0; k < numberOfCardsPerDeck; k++) {
                    Card card = new Card(StringGenerator.cardText(), StringGenerator.cardText(),false, deck);
                    assertTrue(cardService.create(card), "Could not create card");
                    savedCards.put(deck, card);
                }
            }
        }

        // when: loading all cards from database
        List<Card> loadedCards = cardRepository.findAll();

        // then: all saved cards must be found again an attributes must be identical
        for (Map.Entry<Deck,Card> entry : savedCards.entrySet()) {
            Deck deck = entry.getKey();
            Card card = entry.getValue();
            assertTrue(loadedCards.contains(card), "Could not find card " + card);
            Card loadedCard = loadedCards.get(loadedCards.indexOf(card));
            assertEquals(deck, loadedCard.getDeck(), "Wrong deck of " + card);
            assertEquals(card.getFrontText(), loadedCard.getFrontText(), "Wrong front text of " + card);
            assertEquals(card.getBackText(), loadedCard.getBackText(), "Wrong back text of " + card);
            assertEquals(card.isFlipped(), loadedCard.isFlipped(), card + " has been flipped");
        }
    }

    @Test
    public void testGetCardById() {
        // given: a card in the database
        Card card = new Card(
                StringGenerator.deckDescription(),
                StringGenerator.deckDescription(),
                false,
                createDeck("deck-testGetCardById", createUser("person-testGetCardById"))
        );
        assertTrue(cardService.create(card), "Unable to create card");
        UUID id = card.getCardId();

        // when: retrieving card from database by id
        Optional<Card> maybeCard = cardService.findById(id);

        // then: retrieved card must be correct
        assertTrue(maybeCard.isPresent(), "Unable to load card");
        assertEquals(card, maybeCard.get(), "Got card " + maybeCard.get() + " when card " + card + " was expected");
    }

    @Test
    public void testUpdateCard() {
        // given: a card in the database
        // given: a card in the database
        Card card = new Card(
                StringGenerator.deckDescription(),
                StringGenerator.deckDescription(),
                false,
                createDeck("deck-testUpdateCard", createUser("person-testUpdateCard"))
        );
        assertTrue(cardService.create(card), "Unable to create card");

        // when: updating the card
        String frontText = "new front text";
        String backText = "new back text";
        boolean isFlipped = true;
        assertTrue(cardService.update(card, frontText, backText, isFlipped));

        // then: the card should still be available in the repository and attributes should be correct
        Optional<Card> maybeCard = cardService.findById(card.getCardId());
        assertTrue(maybeCard.isPresent(), "Unable to find card");
        Card foundCard = maybeCard.get();
        assertEquals(frontText, foundCard.getFrontText(), "Wrong front text");
        assertEquals(backText, foundCard.getBackText(), "Wrong back text");
        assertEquals(isFlipped, foundCard.isFlipped(), "Card is (not) flipped");
    }
}
