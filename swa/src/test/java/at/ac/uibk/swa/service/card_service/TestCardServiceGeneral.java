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
import at.ac.uibk.swa.util.MockAuthContext;
import at.ac.uibk.swa.util.StringGenerator;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import javax.print.DocFlavor;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
public class TestCardServiceGeneral {
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
    public void saveAndGetCards() {
        // given: demo creators, decks and cards saved to database
        int numberOfCreators = 5;
        int numberOfDecksPerCreator = 5;
        int numberOfCardsPerDeck = 10;

        List<Person> creators = new ArrayList<>();
        Map<Person, Deck> decks = new HashMap<>();
        Map<Deck, Card> savedCards = new HashMap<>();

        for (int i = 0; i < numberOfCreators; i++) {
            Person creator = createUserAndLogin();
            creators.add(creator);
            for (int j = 0; j < numberOfDecksPerCreator; j++) {
                Deck deck = createDeck();
                decks.put(creator, deck);
                for (int k = 0; k < numberOfCardsPerDeck; k++) {
                    Card card = new Card(StringGenerator.cardText(), StringGenerator.cardText(),false);
                    assertTrue(cardService.create(card, deck.getDeckId()), "Could not create card");
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
    public void getCardById() {
        // given: a card in the database
        createUserAndLogin();
        Deck deck = createDeck();
        Card card = new Card(
                StringGenerator.deckDescription(),
                StringGenerator.deckDescription(),
                false
        );
        assertTrue(cardService.create(card, deck.getDeckId()), "Unable to create card");
        UUID id = card.getCardId();

        // when: retrieving card from database by id
        Optional<Card> maybeCard = cardService.findById(id);

        // then: retrieved card must be correct
        assertTrue(maybeCard.isPresent(), "Unable to load card");
        assertEquals(card, maybeCard.get(), "Got card " + maybeCard.get() + " when card " + card + " was expected");
    }

    @Test
    public void updateCard() {
        // given: a card in the database
        createUserAndLogin();
        Deck deck = createDeck();
        Card card = new Card(StringGenerator.deckDescription(), StringGenerator.deckDescription(),false);
        assertTrue(cardService.create(card, deck.getDeckId()), "Unable to create card");

        // when: updating the card
        String frontText = "new front text";
        String backText = "new back text";
        boolean isFlipped = true;
        assertTrue(cardService.update(card.getCardId(), frontText, backText, isFlipped));

        // then: the card should still be available in the repository and attributes should be correct
        Optional<Card> maybeCard = cardService.findById(card.getCardId());
        assertTrue(maybeCard.isPresent(), "Unable to find card");
        Card foundCard = maybeCard.get();
        assertEquals(frontText, foundCard.getFrontText(), "Wrong front text");
        assertEquals(backText, foundCard.getBackText(), "Wrong back text");
        assertEquals(isFlipped, foundCard.isFlipped(), "Card is (not) flipped");
    }

    @Test
    public void updateCardViaCreate() {
        // given: a card in the database
        createUserAndLogin();
        String originalFrontText = StringGenerator.deckDescription();
        String originalBackText = StringGenerator.deckDescription();
        boolean originalIsFlipped = false;
        Deck deck = createDeck();
        Card card = new Card(originalFrontText, originalBackText, originalIsFlipped);
        assertTrue(cardService.create(card, deck.getDeckId()), "Unable to create card");
        UUID id = card.getCardId();

        // when: changing the card by interfering with the card directly
        card.setFrontText("new");
        card.setBackText("new");
        card.setFlipped(true);

        // then: saving the modification via create() should not be possible
        assertFalse(cardService.create(card, deck.getDeckId()), "Changed card got created again");
        Optional<Card> maybeCard = cardService.findById(card.getCardId());
        assertTrue(maybeCard.isPresent(), "Unable to find original card in repository");
        Card foundCard = maybeCard.get();
        assertEquals(originalFrontText, foundCard.getFrontText(), "Front text was changed");
        assertEquals(originalBackText, foundCard.getBackText(), "Back text was changed");
        assertEquals(originalIsFlipped, foundCard.isFlipped(), "Card got flipped");
    }

    @Test
    public void deleteCard() {
        // given: a card in the database
        createUserAndLogin();
        Deck deck = createDeck();
        Card card = new Card( StringGenerator.deckDescription(), StringGenerator.deckDescription(), false);
        assertTrue(cardService.create(card, deck.getDeckId()), "Unable to create card");
        UUID id = card.getCardId();

        // when: deleting the card
        assertTrue(cardService.delete(card.getCardId()));

        // then: card should not be available anymore
        assertTrue(cardService.findById(id).isEmpty(), "Found card");
    }

    @Test
    public void deleteCardViaId() {
        // given: a card in the database
        createUserAndLogin();
        Deck deck = createDeck();
        Card card = new Card( StringGenerator.deckDescription(), StringGenerator.deckDescription(),false);
        assertTrue(cardService.create(card, deck.getDeckId()), "Unable to create card");
        UUID id = card.getCardId();

        // when: deleting the card via id
        assertTrue(cardService.delete(id));

        // then: card should not be available anymore
        assertTrue(cardService.findById(id).isEmpty(), "Found card");
    }
}
