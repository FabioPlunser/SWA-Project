package at.ac.uibk.swa.service;

import at.ac.uibk.swa.models.Card;
import at.ac.uibk.swa.models.Deck;
import at.ac.uibk.swa.repositories.CardRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service("cardService")
public class CardService {
    @Autowired
    CardRepository cardRepository;
    @Autowired
    DeckService deckService;

    /**
     * Gets all existing cards from the repository
     *
     * @return  a list of all cards
     */
    public List<Card> getAllCards() {
        return cardRepository.findAll();
    }

    public boolean save(Card card) {
        try {
            this.cardRepository.save(card);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Gets all existing cards for a specific deck from the repository
     * NOTE: if deck is not found (wrong id) no cards will be returned
     *
     * @param deckId id of the deck for which the cards should be retrieved
     * @return a list of all the cards in the deck
     */
    public List<Card> getAllCards(UUID deckId) {
        List<Card> cards = new ArrayList<>();
        Optional<Deck> maybeDeck = deckService.findById(deckId);
        if (maybeDeck.isPresent()) cards = maybeDeck.get().getCards();
        return cards;
    }
}
