import java.util.UUID;

public class Card {
    private final UUID id;
    private String frontText;
    private String backText;

    public Card(String frontText, String backText) {
        this.id = UUID.randomUUID();
        this.frontText = frontText;
        this.backText = backText;
    }

    private Card(Card card) {
        this.id = card.id;
        this.frontText = card.frontText;
        this.backText = card.backText;
    }

    public Card copy(){
        return new Card(this);
    }

    public UUID getId() {
        return id;
    }

    public String getFrontText() {
        return frontText;
    }

    public void setFrontText(String frontText) {
        this.frontText = frontText;
    }

    public String getBackText() {
        return backText;
    }

    public void setBackText(String backText) {
        this.backText = backText;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Card card = (Card) o;

        return id != null ? id.equals(card.id) : card.id == null;
    }

    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : 0;
    }

    @Override
    public String toString() {
        return "Card{" +
                "id=" + id +
                ", frontText='" + frontText + '\'' +
                ", backText='" + backText + '\'' +
                '}';
    }
}
