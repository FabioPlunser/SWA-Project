package at.ac.uibk.swa.service;

import at.ac.uibk.swa.config.personAuthentication.AuthContext;
import at.ac.uibk.swa.models.*;
import at.ac.uibk.swa.repositories.CardRepository;
import at.ac.uibk.swa.repositories.DeckRepository;
import at.ac.uibk.swa.repositories.LearningProgressRepository;
import at.ac.uibk.swa.service.card_service.learning_algorithm.LearningAlgorithm;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.swing.text.html.Option;
import java.time.LocalDateTime;
import java.util.*;
import java.util.function.Function;
import java.util.function.Predicate;

@Service("cardService")
public class CardService {
    @Autowired
    private CardRepository cardRepository;
    @Autowired
    private UserDeckService userDeckService;
    @Autowired
    private PersonService personService;
    @Autowired
    private DeckRepository deckRepository;
    @Autowired
    private LearningProgressRepository learningProgressRepository;

    /*
    /**
     * Gets all existing cards for a specific deck and a specific user from the repository
     * Depending on deck state:
     *  - !isPublished: only ADMIN and creator will receive cards
     *  - isBlocked:    only ADMIN will receive cards
     *  - isDeleted:    no one will receive cards
     *
     * @param deck deck for which the cards should be retrieved
     * @param person person that is trying to access the cards
     * @return a list of all the cards in the deck

    public List<Card> getAllCards(Deck deck, Person person) {
        if (deck.isDeleted() ||
                (deck.isBlocked() && !person.getPermissions().contains(Permission.ADMIN)) ||
                (!deck.isPublished() && !person.getPermissions().contains(Permission.ADMIN) && !deck.getCreator().equals(person))
        ) {
            return new ArrayList<>();
        } else {
            return deck.getCards();
        }
    }
    */

    /**
     * Gets all existing cards for a specific deck and the currently logged in user
     * Depending on deck state:
     *  - !isPublished: only ADMIN and creator will receive cards
     *  - isBlocked:    only ADMIN will receive cards
     *  - isDeleted:    no one will receive cards
     *
     * @param deckId id of the deck for which the cards should be retrieved
     * @return a list of all the cards in the given deck the specific user can access or nothing if deck does not exist
     */
    public Optional<List<Card>> getAllCards(UUID deckId) {
        Optional<Deck> maybeDeck = userDeckService.findById(deckId);
        if (maybeDeck.isPresent()) {
            Deck deck = maybeDeck.get();
            if (deck.isDeleted() ||
                    (deck.isBlocked() &&
                            !AuthContext.hasPermission(Permission.ADMIN)
                    ) ||
                    (!deck.isPublished() &&
                            !AuthContext.hasPermission(Permission.ADMIN) &&
                            !deck.getCreator().equals(AuthContext.getCurrentUser().orElse(null))
                    )
            ) {
                return Optional.of(new ArrayList<>());
            } else {
                return Optional.of(deck.getCards());
            }
        } else {
            return Optional.empty();
        }
    }

    /*
    /**
     * Gets all cards that should be learnt from a specific deck for the logged in user.
     *
     * @param deck The Deck from which to get the cards to learn.
     * @return A List of cards that are supposed to be learned.
    public List<Card> getAllCardsToLearn(Deck deck) {
        Optional maybeUser = AuthContext.getCurrentUser();

        if (maybeUser.isPresent() && maybeUser.get() instanceof Person person) {
            return getAllCardsToLearn(deck, person);
        }

        return new ArrayList<>();
    }
    */

    /**
     * Gets all cards that should be learnt from a specific deck from the repository for the logged in user
     * User must have subscribed to deck
     *
     * @param deckId The ID of the Deck from which to get the cards to learn.
     * @return A List of cards that are supposed to be learned or nothing if either user does not exist or user has
     * not subscribed to deck
     */
    public Optional<List<Card>> getAllCardsToLearn(UUID deckId) {
        Optional<Authenticable> maybeUser = AuthContext.getCurrentUser();
        if (maybeUser.isPresent() && maybeUser.get() instanceof Person person) {
            if (person.getSavedDecks().stream().anyMatch(d -> d.getDeckId().equals(deckId))) {
                return getAllCards(deckId).map(cards -> cards.stream()
                            .filter(card -> card.getLearningProgress(person)
                                    .map(
                                            // If a Learning Progress is present, check if it's nextLearn is due.
                                            // If it is due, return null => Optional will be empty
                                            lp -> lp.getNextLearn().isBefore(LocalDateTime.now()) ? null : lp
                                    )
                                    // If no Learning Progress is present, the card hasn't been learned.
                                    .isEmpty()
                        ).toList()).or(() -> Optional.of(new ArrayList<>()));
            } else {
                // user has not subscribed to deck
                return Optional.empty();
            }
        } else {
            // no user authenticated
            return Optional.empty();
        }
    }

    /*
    /**
     * Gets all cards that should be learnt from a specific deck from the repository
     * NOTE: if deck is not found (wrong id) no cards will be returned
     *
     * @param deckId the deck from which cards should be returned
     * @param personId the person for which the progress should be checked
     * @return A List of all Cards that have to be learned.
     *         This contains Cards whose nextLearn Date is due and cards which haven't been learned yet.

    public List<Card> getAllCardsToLearn(UUID deckId, UUID personId) {
        Optional<Deck> maybeDeck = userDeckService.findById(deckId);
        Optional<Person> maybePerson = personService.findById(personId);

        if (maybeDeck.isPresent() && maybePerson.isPresent()) {
            return getAllCardsToLearn(maybeDeck.get(), maybePerson.get());
        }

        return new ArrayList<>();
    }

    /**
     * Gets all cards that should be learnt from a specific deck from the repository
     * NOTE: if deck is not found (wrong id) no cards will be returned
     *
     * @param deck the deck from which cards should be returned
     * @param person the person for which the progress should be checked
     * @return A List of all Cards that have to be learned.
     *         This contains Cards whose nextLearn Date is due and cards which haven't been learned yet.

    public List<Card> getAllCardsToLearn(Deck deck, Person person) {
        // Get the current date to compare to the one's stored in the LearningProgress's.
        LocalDateTime now = LocalDateTime.now();
        return getAllCards(deck, person).stream()
                .filter(card -> card.getLearningProgress(person)
                        .map(
                                // If a Learning Progress is present, check if it's nextLearn is due.
                                // If it is due, return null => Optional will be empty
                                lp -> lp.getNextLearn().isBefore(now) ? null : lp
                        )
                        // If no Learning Progress is present, the card hasn't been learned.
                        .isEmpty()
                ).toList();
    }
    */


    /**
     * Finds a card within the repository by its id
     *
     * @param cardId id of the card to be found
     * @return card with given id (if found), otherwise nothing
     */
    public Optional<Card> findById(UUID cardId) {
        return cardRepository.findById(cardId);
    }

    /*
    public Optional<LearningProgress> getLearningProgress(Card card) {
        Optional<Person> maybeUser = AuthContext.getCurrentPerson();
        return maybeUser
                .map(person -> getLearningProgress(card, person))
                .flatMap(Function.identity());
    }
     */

    /**
     * Get the Learning Progress associated with the given Card using the ID and the currently logged in user.
     *
     * @param cardId The ID of the card to get the Learning Progress from.
     * @return The Learning Progress associated with the giv
     */
    public Optional<LearningProgress> getLearningProgress(UUID cardId) {
        Optional<Card> maybeCard = cardRepository.findById(cardId);
        Optional<Authenticable> maybeUser = AuthContext.getCurrentUser();
        if (maybeCard.isPresent() && maybeUser.isPresent() && maybeUser.get() instanceof Person person) {
            return maybeCard.get().getLearningProgress(person);
        } else {
            return Optional.empty();
        }
    }

    /*
    /**
     * Get the Learning Progress associated with the Card and User using their given ID's.
     *
     * @param cardId The ID of the card to get the Learning Progress from.
     * @param personId The ID of the person to get the Learning Progress for.
     * @return The Learning Progress associated with the given Person and Card if it exists.

    public Optional<LearningProgress> getLearningProgress(UUID cardId, UUID personId) {
        Optional<Card> maybeCard = cardRepository.findById(cardId);
        Optional<Person> maybePerson = personService.findById(personId);

        if (maybeCard.isPresent() && maybePerson.isPresent()) {
            return getLearningProgress(maybeCard.get(), maybePerson.get());
        }

        return Optional.empty();
    }

    /**
     * Gets the learning progress for a specific card and a specific person, if available
     *
     * @parm card the card for which the learning progress is requested
     * @param person the person for which the learning progress is requested
     * @return learning progress for given card and person (if found), otherwise nothing

    public Optional<LearningProgress> getLearningProgress(Card card, Person person) {
        return card.getLearningProgress(person);
    }
    */

    /*
    /**
     * Give feedback on the learning of a specific card for a specific person
     *
     * @param cardId The ID of the card to learn.
     * @param personId The ID of the person that is learning.
     * @param difficulty The difficulty that the user gave.
     * @return true if the card was learned, false otherwise.

    public boolean learn(UUID cardId, UUID personId, int difficulty) {
        Optional<Card> maybeCard = findById(cardId);
        Optional<Person> maybePerson = personService.findById(personId);

        if (maybeCard.isPresent() && maybePerson.isPresent()) {
            return learn(maybeCard.get(), maybePerson.get(), difficulty);
        } else {
            return false;
        }
    }

    /**
     * Give feedback on the learning of a specific card for the currently logged-in user.
     *
     * @param card The card to learn.
     * @param difficulty The difficulty that the user gave.
     * @return true if the card was learned, false otherwise.

    public boolean learn(Card card, int difficulty) {
        Optional<Authenticable> maybeUser = AuthContext.getCurrentUser();

        if (maybeUser.isPresent() && maybeUser.get() instanceof Person person) {
            return learn(card, person, difficulty);
        } else {
            return false;
        }
    }
    */

    /**
     * Give feedback on the learning of a specific card for the currently logged-in user.
     * User must have card due for learning
     *
     * @param cardId The ID of the card to learn.
     * @param difficulty The difficulty that the user gave.
     * @return true if the card was learnt, false otherwise.
     */
    public boolean learn(UUID cardId, int difficulty) {
        Optional<Authenticable> maybeUser = AuthContext.getCurrentUser();
        if (maybeUser.isPresent() && maybeUser.get() instanceof Person person) {
            Optional<Card> maybeCard = findById(cardId);
            if (maybeCard.isPresent()) {
                Card card = maybeCard.get();
                Optional<List<Card>> maybeCardsToLearn = getAllCardsToLearn(card.getDeck().getDeckId());
                if (maybeCardsToLearn.isPresent() && maybeCardsToLearn.get().contains(card)) {
                    LearningProgress newLearningProgress = card.updateLearningProgress(
                            person,
                            learningProgress -> LearningAlgorithm.getUpdatedLearningProgress(learningProgress , difficulty)
                    );
                    try {
                        learningProgressRepository.save(newLearningProgress);
                        return save(card) != null;
                    } catch (Exception e) {
                        // learning progress not saved
                        return false;
                    }
                } else {
                    // card not due for learning
                    return false;
                }
            } else {
                // card does not exist
                return false;
            }
        } else {
            // user not logged in
            return false;
        }
    }

    /*
    /**
     * Give feedback on the learning of a specific card and user.
     *
     * @param card The card to learn.
     * @param person The Person that was learning.
     * @param difficulty The difficulty that the user gave.
     * @return true if the card was learned, false otherwise.
    public boolean learn(Card card, Person person, int difficulty) {
        LearningProgress newLp = card.updateLearningProgress(
                person,
                lp -> LearningAlgorithm.getUpdatedLearningProgress(lp, difficulty)
        );

        return learningProgressRepository.save(newLp) != null;
    }
    */

    /**
     * creates a new card within the repository
     * creating user must own the deck specified within the card
     * deck must not be blocked or deleted
     *
     * @param card card to be created
     * @return true if card has been created, false otherwise
     */
    public boolean create(Card card, UUID deckId) {
        if (card != null && card.getCardId() == null) {
            Deck deck = getDeckIfWriteAccess(deckId).orElse(null);
            if (deck != null) {
                card.setDeck(deck);
                return save(card) != null;
            } else {
                // logged in user has not created the given deck or deck is blocked/deleted - therefore no write access
                return false;
            }
        } else {
            // no card given or already created card given
            return false;
        }
    }

    /**
     * Saves a card to the repository
     *
     * @param card card to be saved
     * @return saved card if it has been saved, null otherwise
     */
    private Card save(Card card) {
        try {
            return cardRepository.save(card);
        } catch (Exception e) {
            return null;
        }
    }

    /*
    /**
     * Updates a card with the given parameters
     * NOTE: No permission check is done within this method - check before, if execution is allowed!
     *
     * @param card card to be updated
     * @param frontText new front text of the card, set to null if no change is desired
     * @param backText new back text of the card, set to null if no change is desired
     * @param isFlipped card flipped or not flipped
     * @return true if card has been updated, false otherwise
    public boolean update(Card card, String frontText, String backText, boolean isFlipped) {
        if (card != null && card.getCardId() != null) {
            if (frontText !=  null) card.setFrontText(frontText);
            if (backText != null) card.setBackText(backText);
            card.setFlipped(isFlipped);
            return save(card) != null;
        } else {
            return false;
        }
    }
    */

    /**
     * Updates a card with the given parameters
     * Checks write access to deck of card
     *
     * @param cardId id of the card to update
     * @param frontText new front text of the card, set to null if no change is desired
     * @param backText new back text of the card, set to null if no change is desired
     * @param isFlipped card flipped or not flipped
     * @return true if card has been updated, false otherwise
     */
    public boolean update(UUID cardId, String frontText, String backText, boolean isFlipped) {
        Optional<Card> maybeCard = findById(cardId);
        if (maybeCard.isPresent()) {
            Card card = maybeCard.get();
            if (getDeckIfWriteAccess(card.getDeck().getDeckId()).isPresent()) {
                if (frontText !=  null) card.setFrontText(frontText);
                if (backText != null) card.setBackText(backText);
                card.setFlipped(isFlipped);
                return save(card) != null;
            } else {
                // no write access to given deck
                return false;
            }
        } else {
            // card not found
            return false;
        }
    }

    /*
    /**
     * Deletes a card from the repository (hard delete)
     *
     * @param card card to be deleted
     * @return true if card has been updated, false otherwise
    public boolean delete(Card card) {
        try {
            card.getDeck().getCards().remove(card);
            deckRepository.save(card.getDeck());
            cardRepository.delete(card);
            return true;
        } catch (Exception e) {
            return false;
        }
    }
    */

    /**
     * Deletes a card from the repository (hard delete)
     * Checks write access to deck of card
     *
     * @param cardId id of the card to be deleted
     * @return true if card has been updated, false otherwise
     */
    public boolean delete(UUID cardId) {
        Optional<Card> maybeCard = findById(cardId);
        if (maybeCard.isPresent()) {
            Card card = maybeCard.get();
            Deck deck = getDeckIfWriteAccess(card.getDeck().getDeckId()).orElse(null);
            if (deck != null) {
                deck.getCards().remove(card);
                try {
                    deckRepository.save(deck);
                    cardRepository.delete(card);
                    return true;
                } catch (Exception e) {
                    return false;
                }
            } else {
                // no write access to given deck
                return false;
            }
        } else {
            // card not found
            return false;
        }
    }

    /**
     * Check if the currently logged in user has write access to the given deck
     *
     * @param deckId id of the deck to check
     * @return the deck if write access, otherwise nothing
     */
    private Optional<Deck> getDeckIfWriteAccess(UUID deckId) {
        Optional<Authenticable> maybeUser = AuthContext.getCurrentUser();
        if (maybeUser.isPresent() && maybeUser.get() instanceof Person person) {
            return person.getCreatedDecks().stream()
                    .filter(Predicate.not(Deck::isDeleted))
                    .filter(Predicate.not(Deck::isBlocked))
                    .filter(d -> d.getDeckId().equals(deckId))
                    .findFirst();
        } else {
            // no user authenticated
            return Optional.empty();
        }
    }
}
