package at.ac.uibk.swa.Service;

import at.ac.uibk.swa.Models.Card;
import at.ac.uibk.swa.Models.Deck;
import at.ac.uibk.swa.Repositories.CardRepository;
import at.ac.uibk.swa.Repositories.DeckRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service("cardService")
public class CardService {

    @Autowired
    CardRepository cardRepository;

    public boolean save(Card card) {
        try {
            this.cardRepository.save(card);
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}
