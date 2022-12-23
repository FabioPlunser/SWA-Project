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
    public void testSaveDecks() {
    }
}
