# How we handle decks that are publicly available

## Short overview

We've decided to have the content of a deck (i.e. the cards it contains) in a separate class that is independent from the learning progress of that deck. When a User decides to add a deck (created by another User) to his collection, the deck is wrapped inside an object that also contains the learning progress for each card of that deck (specific to the User). That way we can have multiple learning progresses for each deck.

## Specific description

*This section references the UML domain model of our project and the DemoApp, which is a basic implementation of our domain model.*

First of all, cards on their own are implemented in the Card class, which has an Id, a front text and a back text.

The basic functions and attributes of a deck are implemented in the Deck class, which implements the IDeck interface (more on that and why we need it in the next section). Among other things, the Deck class contains the ID of the creator, a name, a List of Card objects it contains and a Boolean variable `published`.

The LearningProgress class contains the important values for our learning algorithm and is always correlated to only one User and one Card.

The class which brings them all together is the DeckView class, which contains a reference to an IDeck object (the Deck it represents) and a Map which stores a LearningProgress object for each Card object of the deck.

When the creator of a Deck wants to make it public, he can set the `published` variable to true. Then every User on the platform can find and add the Deck.

When a User adds a Deck to his collection, a new DeckView object is created, with a new LearningProgress object for each Card of the Deck.

## What happens when a set is deleted

*This section references the UML domain model of our project and the DemoApp, which is a basic implementation of our domain model.*

With this implementation however, we need take into consideration what happens when a deck is deleted. If we just delete the object that represents that deck, every user that added that deck to his collection would still have the reference stored on his side, which now becomes invalid. There are different approaches to the problem:

-   For each created deck, there is a list of users that added the deck, who will be notified when the deck is deleted. Then the reference to the deck can also be deleted by every user that has added the deck. (Observer Pattern)
-   Every time a user loads his saved decks, every reference is checked for `NULL` value, and then just quietly deleted. This is a very simple approach, but could lead to some confusion for the users.
-   We don't really delete the deck, only set some parameter to make it unavailable. That way for example, the user could still see the title of the deck, but is shown that it was deleted. The disadvantage of this approach is that the deleted decks still take up memory, when it is not really needed.

We have decided on a combination of the first and third approach. The Deck class contains the two attributes: `blocked` and `deleted`. The creator of a deck can set the `deleted` variable to `true` to mark it as deleted, and admins can set the `blocked` variable to `true`, when it doesn't adhere to the guidelines.

To prevent Users from accessing the deck when one of these variables is set to `true`, we created the DeckProxy class (which also implements the IDeck Interface) to do the access control.

Additionally, we also implemented the Observer Pattern (first approach) to notify the Users when an admin blocks a set. The message in the `notify()` method can also be used to inform the Users of the reason the set was blocked.
