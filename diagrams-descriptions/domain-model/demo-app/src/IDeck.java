import java.util.List;
import java.util.UUID;

public interface IDeck extends ISubject {
    boolean hasModifyAccess();
    boolean hasAdminAccess();
    UUID getCreatorId();
    void setName(String name) throws IllegalAccessException;
    String getName();
    void setDescription(String description) throws IllegalAccessException;
    String getDescription();
    void addCard(Card card) throws IllegalAccessException;
    void deleteCard(Card card) throws IllegalAccessException;
    List<UUID> getAllCardIds() throws IllegalAccessException;
    Card getCard(UUID cardId) throws IllegalAccessException;
    void publish() throws IllegalAccessException;
    void unpublish() throws IllegalAccessException;
    boolean isPublished();
    void block() throws IllegalAccessException;
    void unblock() throws IllegalAccessException;
    boolean isBlocked();
    void delete() throws IllegalAccessException;
    boolean isDeleted();
}
