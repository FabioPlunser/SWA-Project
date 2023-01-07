export interface IDeck {
    deckId: string;
    name: string;
    cards: Card[];
    [key: string]: any;
}

export interface Card {
    cardId: string;
    frontText: string, 
    backText: string,
    [key: string]: any;
}