import java.time.LocalDate;
import java.util.*;

public class DeckView {
    private final IDeck deck;
    private Map<UUID, LearningProgress> progress = new HashMap<>();

    public DeckView(IDeck deck, UUID userId, boolean isAdmin) {
        this.deck = new DeckProxy(deck, userId, isAdmin);
    }

    public IDeck getDeck() {
        return this.deck;
    }

    private void checkForNewCards() throws IllegalAccessException {
        for (UUID cardId : deck.getAllCardIds()) {
            if (!progress.containsKey(cardId)) {
                progress.put(cardId, new LearningProgress());
            }
        }

        List<UUID> cardIdsInDeck = deck.getAllCardIds();
        progress.keySet().removeIf(cardId -> !cardIdsInDeck.contains(cardId));
    }

    public List<Card> getAllCardsToLearn() throws IllegalAccessException {
        checkForNewCards();

        List<UUID> cardIdsToLearn = progress
                .entrySet().stream()
                .filter(x -> !x.getValue().getNextLearningDate().toLocalDate().isAfter(LocalDate.now()))
                .sorted(Comparator.comparing(x -> x.getValue().getNextLearningDate()))
                .map(Map.Entry::getKey)
                .toList();

        List<Card> cardsToLearn = new ArrayList<>();

        for (UUID cardIdToLearn : cardIdsToLearn) {
            cardsToLearn.add(deck.getCard(cardIdToLearn));
        }

        return cardsToLearn;
    }

    public void learn(UUID cardId, int difficulty) throws IllegalAccessException {
        checkForNewCards();

        if (!progress.containsKey(cardId)) {
            System.out.println(cardId);
            throw new NoSuchElementException("Learning progress for card not found");
        }
        progress.get(cardId).giveFeedback(difficulty);
    }
}
