import java.util.List;
import java.util.UUID;

public class DemoApp {
    public static void main(String[] args) throws IllegalAccessException {
        // create sample users
        User admin = new User("admin", true);
        User creator = new User("creator", false);
        User viewer = new User("viewer", false);

        // create a new deck, set user 'creator' as creator
        Deck deck = new Deck(creator.getId(), "Test", "This is a demo deck");
        deck.addCard(new Card("frontText 1", "backText 1"));
        deck.addCard(new Card("frontText 2", "backText 2"));
        deck.addCard(new Card("frontText 3", "backText 3"));

        // add deck views for all users
        admin.addDeckView(deck);
        creator.addDeckView(deck);
        viewer.addDeckView(deck);

        // temporary for further demo
        User user = creator;

        // display cards to learn
        List<Card> cardsToLearn;
        cardsToLearn = user.getAllDeckViews().get(0).getAllCardsToLearn();
        System.out.println("## Cards to learn: ");
        System.out.println(cardsToLearn);
        System.out.println();

        // learn first card and display cards to learn again
        int difficulty = 2;
        System.out.println("## Learnt first card with difficulty " + difficulty + ". New cards to learn today:");
        user.getAllDeckViews().get(0).learn(cardsToLearn.get(0).getId(),difficulty);
        cardsToLearn = user.getAllDeckViews().get(0).getAllCardsToLearn();
        System.out.println(cardsToLearn);
        System.out.println();

        // learn first card and display cards to learn again
        difficulty = 5;
        System.out.println("## Learnt first card with difficulty " + difficulty + ". New cards to learn today:");
        user.getAllDeckViews().get(0).learn(cardsToLearn.get(0).getId(),difficulty);
        cardsToLearn = user.getAllDeckViews().get(0).getAllCardsToLearn();
        System.out.println(cardsToLearn);
        System.out.println();

        // add a card and display cards to learn again
        System.out.println("## Adding a card. New cards to learn today:");
        user.getAllDeckViews().get(0).getDeck().addCard(new Card("frontText 4", "backText 4"));
        cardsToLearn = user.getAllDeckViews().get(0).getAllCardsToLearn();
        System.out.println(cardsToLearn);
        System.out.println();

        // remove a card and display cards to learn again
        System.out.println("## Remove a card. New cards to learn today:");
        Card toRemove = user.getAllDeckViews().get(0).getAllCardsToLearn().get(1);
        user.getAllDeckViews().get(0).getDeck().deleteCard(toRemove);
        cardsToLearn = user.getAllDeckViews().get(0).getAllCardsToLearn();
        System.out.println(cardsToLearn);
        System.out.println();

        // block deck -> all users with view on deck should receive notification
        System.out.println("## Blocking deck");
        admin.getAllDeckViews().get(0).getDeck().block();
        System.out.println();

        // trying to display blocked deck
        System.out.println("## Displaying blocked deck");
        System.out.println(user.getAllDeckViews().get(0).getDeck());
        System.out.println();

        // unblocking deck, then trying to display blocked deck again
        System.out.println("## Unblocking deck, then displaying unblocked deck");
        admin.getAllDeckViews().get(0).getDeck().unblock();
        System.out.println(user.getAllDeckViews().get(0).getDeck());
        System.out.println();

        // switching to viewer, then trying to display deck that has not yet been published
        System.out.println("## Switching to user 'viewer', then displaying deck, that is not yet published");
        user = viewer;
        System.out.println(user.getAllDeckViews().get(0).getDeck());
        System.out.println();

        // publishing deck, then trying to display deck again
        System.out.println("## Publishing deck, then displaying deck again");
        user = creator;
        user.getAllDeckViews().get(0).getDeck().publish();
        user = viewer;
        System.out.println(user.getAllDeckViews().get(0).getDeck());
        System.out.println();

        // displaying cards to learn today by different user
        System.out.println("## Displaying cards to learn today by user 'viewer'");
        System.out.println(user.getAllDeckViews().get(0).getAllCardsToLearn());
        System.out.println();

        // trying to change the text of a foreign deck
        System.out.println("## Trying to change text of card of foreign deck");
        UUID cardId = user.getAllDeckViews().get(0).getDeck().getAllCardIds().get(0);
        Card card = user.getAllDeckViews().get(0).getDeck().getCard(cardId);
        System.out.println("Original card: " + card);
        String newFrontText = "This is the changed front text";
        System.out.println("Trying to change front text to: " + newFrontText);
        user.getAllDeckViews().get(0).getDeck().getCard(cardId).setFrontText("This is the changed front text");
        System.out.println("Updated card: " + user.getAllDeckViews().get(0).getDeck().getCard(cardId));
        System.out.println();

        // trying to change the text of an owned deck
        user = creator;
        System.out.println("## Trying to change text of card of own deck");
        cardId = user.getAllDeckViews().get(0).getDeck().getAllCardIds().get(0);
        card = user.getAllDeckViews().get(0).getDeck().getCard(cardId);
        System.out.println("Original card: " + card);
        newFrontText = "This is the changed front text";
        System.out.println("Trying to change front text to: " + newFrontText);
        user.getAllDeckViews().get(0).getDeck().getCard(cardId).setFrontText("This is the changed front text");
        System.out.println("Updated card: " + user.getAllDeckViews().get(0).getDeck().getCard(cardId));
        System.out.println();

        // accessing card from viewing user
        user = viewer;
        System.out.println("## Viewing card from viewing user");
        System.out.println("Updated card: " + user.getAllDeckViews().get(0).getDeck().getCard(cardId));
        System.out.println();

        // trying to unpublish deck as admin
        user = admin;
        System.out.println("## Trying to unpublish card as admin");
        try {
            user.getAllDeckViews().get(0).getDeck().unpublish();
        } catch (Exception e) {
            System.out.println("Caught exception: " + e.getMessage());
        }
        System.out.println();

        // unpublishing deck as creator
        user = creator;
        System.out.println("## Trying to unpublish card as creator");
        try {
            user.getAllDeckViews().get(0).getDeck().unpublish();
            System.out.println("Unpublished deck");
        } catch (Exception e) {
            System.out.println("Caught exception: " + e.getMessage());
        }
        System.out.println();

        // checking deck as viewer
        user = viewer;
        System.out.println("## Displaying deck as viewer");
        System.out.println(user.getAllDeckViews().get(0).getDeck());
        System.out.println();

        // try to access cards of deleted deck
        System.out.println("## Trying to access cards of unpublished deck");
        try {
            System.out.println(user.getAllDeckViews().get(0).getAllCardsToLearn());
        } catch (Exception e) {
            System.out.println("Caught exception: " + e.getMessage());
        }
        System.out.println();

        // removing deck view as creator
        user = creator;
        System.out.println("## Removing deck view as creator");
        user.removeDeckView(user.getAllDeckViews().get(0));
        System.out.println("Deck view removed");
        System.out.println();

        // checking deck as viewer
        user = viewer;
        System.out.println("## Displaying deck as viewer");
        System.out.println(user.getAllDeckViews().get(0).getDeck());
        System.out.println();

        // try to access cards of deleted deck
        System.out.println("## Trying to access cards of deleted deck");
        try {
            System.out.println(user.getAllDeckViews().get(0).getAllCardsToLearn());
        } catch (Exception e) {
            System.out.println("Caught exception: " + e.getMessage());
        }
        System.out.println();
    }
}
