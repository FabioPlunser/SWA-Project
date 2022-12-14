import java.util.List;
import java.util.UUID;

public class DeckProxy implements IDeck{
    private final IDeck deck;
    private final UUID personId;
    private final boolean userIsAdmin;

    public DeckProxy(IDeck deck, UUID personId, boolean userIsAdmin) {
        this.deck = deck;
        this.personId = personId;
        this.userIsAdmin = userIsAdmin;
    }

    @Override
    public UUID getCreatorId() {
        return deck.getCreatorId();
    }

    private boolean hasReadAccess() {
        // admin can view deck as long as it's not deleted
        if (hasAdminAccess()) {
            return !deck.isDeleted();
        // creator can view deck as long as it's not deleted and not blocked
        } else if (personId == deck.getCreatorId()) {
            return !deck.isBlocked() && !deck.isDeleted();
        // others can view deck only as long as it's published, not deleted and not blocked
        } else {
            return deck.isPublished() && !deck.isBlocked() && !deck.isDeleted();
        }
    }

    public boolean hasModifyAccess() {
        return hasReadAccess() && personId == deck.getCreatorId() ;
    }

    @Override
    public boolean hasAdminAccess() {
        return userIsAdmin;
    }

    @Override
    public void setName(String name) throws IllegalAccessException{
        if (!hasModifyAccess()) {
            throw new IllegalAccessException("Only creator is allowed to change deck properties");
        }

        deck.setName(name);
    }

    @Override
    public String getName() {
        return deck.getName();
    }

    @Override
    public void setDescription(String description) throws IllegalAccessException{
        if (!hasModifyAccess()) {
            throw new IllegalAccessException("Only creator is allowed to change deck properties");
        }

        deck.setDescription(description);
    }

    @Override
    public String getDescription() {
        if (!hasReadAccess()) {
            if (deck.isDeleted()) {
                return "Deck has been deleted";
            } else if (deck.isBlocked()) {
                return "Deck has been blocked by administrator";
            } else if (!deck.isPublished()) {
                return "Deck has been set to private by creator";
            } else {
                return "Deck is inaccessible";
            }
        }

        return deck.getDescription();
    }

    @Override
    public void addCard(Card card) throws IllegalAccessException {
        if (!hasModifyAccess()) {
            throw new IllegalAccessException("Not allowed to modify deck");
        }

        deck.addCard(card);
    }

    @Override
    public void deleteCard(Card card) throws IllegalAccessException {
        if (!hasModifyAccess()) {
            throw new IllegalAccessException("Not allowed to modify deck");
        }

        deck.deleteCard(card);
    }

    @Override
    public List<UUID> getAllCardIds() throws IllegalAccessException {
        if (!hasReadAccess()) {
            throw new IllegalAccessException("Not allowed to view deck");
        }

        return deck.getAllCardIds();
    }

    @Override
    public Card getCard(UUID cardId) throws IllegalAccessException{
        if (!hasReadAccess()) {
            throw new IllegalAccessException("Not allowed to view deck");
        }

        if (hasModifyAccess()) {
            return deck.getCard(cardId);
        } else {
            return deck.getCard(cardId).copy();
        }
    }

    @Override
    public void publish() throws IllegalAccessException {
        if (!hasModifyAccess()) {
            throw new IllegalAccessException("Not allowed to publish deck");
        }

        deck.publish();
    }

    @Override
    public void unpublish() throws IllegalAccessException {
        if (!hasModifyAccess()) {
            throw new IllegalAccessException("Not allowed to unpublish deck");
        }

        deck.unpublish();
    }

    @Override
    public boolean isPublished() {
        return deck.isPublished();
    }

    @Override
    public void block() throws IllegalAccessException {
        if (!hasAdminAccess()) {
            throw new IllegalAccessException("Not allowed to block deck");
        }

        deck.block();
    }

    @Override
    public void unblock() throws IllegalAccessException {
        if (!hasAdminAccess()) {
            throw new IllegalAccessException("Not allowed to unblock deck");
        }

        deck.unblock();
    }

    @Override
    public boolean isBlocked() {
        return deck.isBlocked();
    }

    @Override
    public void delete() throws IllegalAccessException {
        if (!hasModifyAccess()) {
            throw new IllegalAccessException("Not allowed to delete deck");
        }

        deck.delete();
    }

    @Override
    public boolean isDeleted() {
        return deck.isDeleted();
    }

    @Override
    public String toString() {
        return "Deck{" +
                "name='" + getName() + '\'' +
                ", description='" + getDescription() + '\'' +
                '}';
    }

    @Override
    public void registerObserver(IObserver observer) {
        deck.registerObserver(observer);
    }

    @Override
    public void removeObserver(IObserver observer) {
        deck.removeObserver(observer);
    }

    @Override
    public void notifyObservers(String message) {
        deck.notifyObservers(message);
    }
}
