package at.ac.uibk.swa.service;

import at.ac.uibk.swa.models.Card;
import at.ac.uibk.swa.models.Deck;
import at.ac.uibk.swa.models.LearningProgress;
import at.ac.uibk.swa.models.Person;
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
    Learn
    @Autowired
    DeckService deckService;
    @Autowired
    PersonService personService;

    /**
     * Gets all existing cards from the repository
     *
     * @return  a list of all cards
     */
    public List<Card> getAllCards() {
        return cardRepository.findAll();
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

    /**
     * Finds a card within the repository by its id
     *
     * @param cardId id of the card to be found
     * @return card with given id (if found), otherwise nothing
     */
    public Optional<Card> findById(UUID cardId) {
        return cardRepository.findById(cardId);
    }

    /**
     * Gets the learning progress for a specific card and a specific person, if available
     *
     * @param personId id of the person for which the learning progress is requested
     * @return learning progress for given card and person (if found), otherwise nothing
     */
    public Optional<LearningProgress> getLearningProgress(UUID cardId, UUID personId) {
        Optional<Card> maybeCard = findById(cardId);
        Optional<Person> maybePerson = personService.findById(personId);
        if (maybeCard.isPresent() && maybePerson.isPresent()) {
            Card card = maybeCard.get();
            Person person = maybePerson.get();
            if (card.getLearningProgresses().containsKey(person)) {
                return Optional.of(card.getLearningProgresses().get(person));
            } else {
                return Optional.empty();
            }
        } else {
            return Optional.empty();
        }
    }

    /**
     * Saves a card to the repository
     *
     * @param card card to be saved
     * @return true if card has been saved, false otherwise
     */
    public boolean save(Card card) {
        try {
            this.cardRepository.save(card);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Updates a card with the given parameters
     * NOTE: No permission check is done within this method - check before, if execution is allowed!
     *
     * @param cardId id of the card to be updated
     * @param frontText new front text of the card, set to null if no change is desired
     * @param backText new back text of the card, set to null if no change is desired
     * @param isFlipped card flipped or not flipped
     * @return true if card has been updated, false otherwise
     */
    public boolean update(UUID cardId, String frontText, String backText, boolean isFlipped) {
        try {
            Optional<Card> maybeCard = findById(cardId);
            if (maybeCard.isEmpty()) {
                return false;
            } else {
                Card card = maybeCard.get();
                if (frontText !=  null) card.setFrontText(frontText);
                if (backText != null) card.setBackText(backText);
                card.setFlipped(isFlipped);
                return save(card);
            }
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Deletes a card from the repository (hard delete)
     *
     * @param cardId id of the card to be deleted
     * @return true if card has been updated, false otherwise
     */
    public boolean delete(UUID cardId) {
        try {
            cardRepository.deleteById(cardId);
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}
