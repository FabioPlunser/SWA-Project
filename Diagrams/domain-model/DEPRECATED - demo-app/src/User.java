import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;

public class User implements IObserver{
    private final UUID id;
    private String username;
    private String emailAddress;
    private String passwordHash;
    private boolean admin;
    private List<DeckView> deckViews = new ArrayList<>();

    public User(String username, boolean isAdmin) {
        this.id = UUID.randomUUID();
        this.username = username;
        this.admin = isAdmin;
    }

    public UUID getId() {
        return id;
    }

    public boolean isAdmin() {
        return admin;
    }

    public void addDeckView(Deck deck) {
        deckViews.add(new DeckView(deck, id, admin));
        deck.registerObserver(this);
    }

    public void removeDeckView(DeckView deckView) throws IllegalAccessException{
        if (!deckViews.contains(deckView)) {
            throw new NoSuchElementException("Deck view not found");
        }
        if (deckView.getDeck().hasModifyAccess()) {
            try {
                deckView.getDeck().delete();
            } catch (IllegalAccessException ignored) {
                throw new IllegalAccessException("Unable to delete deck, although deleting should be possible");
            }
        }
        deckView.getDeck().removeObserver(this);
        deckViews.remove(deckView);
    }

    public List<DeckView> getAllDeckViews() {
        return deckViews;
    }

    @Override
    public void update(String message) {
        if (!admin) {
            System.out.println("Notification for " + username + ": " + message);
        }
    }
}
