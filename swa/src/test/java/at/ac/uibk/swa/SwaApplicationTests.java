package at.ac.uibk.swa;

import at.ac.uibk.swa.Models.Card;
import at.ac.uibk.swa.Models.Customer;
import at.ac.uibk.swa.Models.Deck;
import at.ac.uibk.swa.Service.CardService;
import at.ac.uibk.swa.Service.CustomerService;
import at.ac.uibk.swa.Service.DeckService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
class SwaApplicationTests {

    @Autowired
    private CustomerService customerService;
    @Autowired
    private DeckService deckService;
    @Autowired
    private CardService cardService;

    @Test
    public void DummyTest() {
        assertEquals(2 + 2, 4);
    }

    @Test
    public void TestRetrievingUserFromToken() {
        Customer customer = new Customer("tokenTest", "def", "ghi", false);

        assertEquals(true, customerService.save(customer));

        Optional<UUID> oToken = customerService.login(customer.getUsername(), customer.getPasswdHash());
        assertTrue(oToken.isPresent());
        UUID token = oToken.get();

        Optional<Customer> oCustomer = customerService.findByToken(token);
        assertTrue(oCustomer.isPresent());
        assertEquals(customer.getCustomerId(), oCustomer.get().getCustomerId());
    }

    @Test
    public void TestCreatingDeck() {
        Customer customer = new Customer("deckTest", "def", "ghi", false);

        Deck deck = new Deck("Deck1", "abcd", customer);
        Card card = new Card("Front", "Back", false, deck);

        assertEquals(true, customerService.save(customer));
        assertEquals(true, deckService.save(deck));
        assertEquals(true, cardService.save(card));

        customer = customerService.findByToken(customerService.login(customer.getUsername(), customer.getPasswdHash()).get()).get();

        assertEquals(deck.getDeckId(), customer.getDecks().get(0).getDeckId());
        assertEquals(1, customer.getDecks().size());

        assertEquals(card.getCardId(), customer.getDecks().get(0).getCards().get(0).getCardId());
        assertEquals(1, customer.getDecks().get(0).getCards().size());
    }
}
