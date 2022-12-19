import java.util.*;

public class Deck implements IDeck{
    private final UUID id;
    private final UUID creatorId;
    private String name;
    private String description;
    private boolean published;
    private boolean blocked;
    private boolean deleted;
    private List<Card> cards = new ArrayList<>();
    private List<IObserver> observers = new ArrayList<>();

    public Deck(UUID creatorId, String name, String description) {
        this.id = UUID.randomUUID();
        this.creatorId = creatorId;
        this.name = name;
        this.description = description;
    }

    public UUID getCreatorId() {
        return this.creatorId;
    }

    @Override
    public boolean hasModifyAccess() {
        return true;
    }

    @Override
    public boolean hasAdminAccess() {
        return true;
    }

    @Override
    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String getName() {
        return this.name;
    }

    @Override
    public void setDescription(String description) {
        this.description = description;
    }

    @Override
    public String getDescription() {
        return this.description;
    }

    @Override
    public void addCard(Card card) {
        if (cards.contains(card)) {
            throw new IllegalArgumentException("Card is already part of deck");
        }

        cards.add(card.copy());
    }

    @Override
    public void deleteCard(Card card) {
        if (!cards.contains(card)) {
            throw new NoSuchElementException("Card not found in deck");
        }

        cards.remove(card);
    }

    @Override
    public List<UUID> getAllCardIds() {
        return cards.stream().map(Card::getId).toList();
    }

    @Override
    public Card getCard(UUID cardId) {
        if (getAllCardIds().contains(cardId)) {
            return cards.stream().filter(c -> c.getId() == cardId).reduce((first, second) -> first).orElse(null);
        } else {
            throw new NoSuchElementException("Card with id " + cardId + " not found in deck");
        }
    }

    public boolean isPublished() {
        return published;
    }

    @Override
    public void publish() {
        published = true;
    }

    @Override
    public void unpublish() {
        published = false;
    }

    public boolean isBlocked() {
        return blocked;
    }

    @Override
    public void block() {
        if (!isBlocked()) {
            notifyObservers("Deck '" + name + "' has been blocked by an administrator");
        }
        blocked = true;
    }

    @Override
    public void unblock() {
        blocked = false;
    }

    public boolean isDeleted() {
        return deleted;
    }

    @Override
    public void delete() {
        deleted = true;
    }

    @Override
    public String toString() {
        return "Deck{" +
                "name='" + name + '\'' +
                ", description='" + description + '\'' +
                '}';
    }

    @Override
    public void registerObserver(IObserver observer) {
       if (!observers.contains(observer)) {
           observers.add(observer);
       }
    }

    @Override
    public void removeObserver(IObserver observer) {
        observers.remove(observer);
    }

    @Override
    public void notifyObservers(String message) {
        for (IObserver observer : observers) {
            observer.update(message);
        }
    }
}
