package at.ac.uibk.swa.service.admin_deck_service;

import at.ac.uibk.swa.models.Deck;
import at.ac.uibk.swa.models.Permission;
import at.ac.uibk.swa.models.Person;
import at.ac.uibk.swa.service.AdminDeckService;
import at.ac.uibk.swa.service.PersonService;
import at.ac.uibk.swa.service.UserDeckService;
import at.ac.uibk.swa.util.StringGenerator;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
@ActiveProfiles("test")
public class AdminDeckServiceGeneral {
    @Autowired
    AdminDeckService adminDeckService;
    @Autowired
    UserDeckService userDeckService;
    @Autowired
    PersonService personService;

    private Deck createDeck(String name, String creatorName) {
        Person creator = new Person(creatorName, StringGenerator.email(), StringGenerator.password(), Set.of(Permission.USER));
        assertTrue(personService.create(creator), "Unable to create user");
        Deck deck = new Deck(StringGenerator.deckDescription(), StringGenerator.deckDescription(), creator);
        assertTrue(userDeckService.create(deck), "Unable to create deck");
        return deck;
    }

    @Test
    public void testGetDeckById() {
        // given: a deck in the repository
        Deck deck = createDeck("deck-testGetDeckById", "person-testGetDeckById");
        UUID id = deck.getDeckId();

        // when: trying to load that deck by id from the repository
        Optional<Deck> maybeDeck = adminDeckService.findById(id);

        // then: retrieved deck must be correct
        assertTrue(maybeDeck.isPresent(), "Unable to find deck");
        assertEquals(deck, maybeDeck.get(), "Got deck " + maybeDeck.get() + " when deck " + deck + " was expected");
    }

    @Test
    public void testBlockDeck() {

    }

    @Test
    public void testUnblockDeck() {

    }

    @Test
    public void testFindAllDecks() {

    }
}
