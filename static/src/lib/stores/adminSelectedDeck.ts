import {writable} from 'svelte/store';

const Store = localStorage.getItem("adminSelectedDeck");
export const adminSelectedDeck = writable(JSON.parse(Store) || null);
adminSelectedDeck.subscribe(value => {
    localStorage.setItem("adminSelectedDeck", JSON.stringify(value));
});