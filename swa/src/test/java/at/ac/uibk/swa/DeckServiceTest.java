package at.ac.uibk.swa;

import at.ac.uibk.swa.models.Deck;
import at.ac.uibk.swa.models.Person;
import at.ac.uibk.swa.service.CardService;
import at.ac.uibk.swa.service.DeckService;
import at.ac.uibk.swa.service.PersonService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

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
    public void testCreateDeck() {
        // given: person and deck created by person
        Person person = new Person("person-testCreatingDeck", "", "", Set.of());
        Deck deck = new Deck("deck-testCreatingDeck", "", person);
        assertTrue(personService.save(person), "Unable to save user for test");
        assertTrue(deckService.save(deck), "Unable to save deck for test");

        // when: loading person and created decks
        Optional<Person> maybePerson = personService.login(person.getUsername(), person.getPasswdHash());
        assertTrue(maybePerson.isPresent(), "Unable to login user");
        List<Deck> createdDecks = maybePerson.get().getCreatedDecks();

        // then: user should have exactly one created deck and deck id should be as defined in beginning
        assertEquals(1, createdDecks.size(), "Unexpectedly received " + createdDecks.size() + " decks");
        assertEquals(deck.getDeckId(), createdDecks.get(0).getDeckId(), "Got deck " + createdDecks.get(0).getDeckId() + " when deck " + deck.getDeckId() + " was expected");
    }
}
