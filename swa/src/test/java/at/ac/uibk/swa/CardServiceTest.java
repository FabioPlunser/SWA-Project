package at.ac.uibk.swa;

import at.ac.uibk.swa.service.CardService;
import at.ac.uibk.swa.service.DeckService;
import at.ac.uibk.swa.service.PersonService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

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
    public void testDummy() {
        assertEquals(5, 4+1);
    }
}
