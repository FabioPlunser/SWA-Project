package at.ac.uibk.swa.Service;

import at.ac.uibk.swa.Models.Deck;
import at.ac.uibk.swa.Repositories.DeckRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service("deckService")
public class DeckService {

    @Autowired
    DeckRepository deckRepository;

    public boolean save(Deck deck) {
        try {
            this.deckRepository.save(deck);
            return true;
        } catch (Exception e) {
            throw e;
        }
    }
}
