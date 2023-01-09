package at.ac.uibk.swa.models.rest_responses;

import at.ac.uibk.swa.models.Deck;
import at.ac.uibk.swa.models.Person;
import lombok.Getter;
import lombok.experimental.SuperBuilder;

import java.io.Serializable;
import java.util.List;

@Getter
@SuperBuilder
public class DeckListResponse extends ListResponse<DeckListResponse.DeckInfo> implements Serializable {

    private static String type = "DeckList";

    public DeckListResponse(List<Deck> decks) {
        super(decks.stream().map(deck -> new DeckInfo(deck)).toList());
    }

    public static class DeckInfo implements Serializable {

        private Deck deck;
        private int numCards;

        public DeckInfo(Deck deck) {
            this.deck = deck;
            this.numCards = this.deck.getCards().size();
        }
    }
}