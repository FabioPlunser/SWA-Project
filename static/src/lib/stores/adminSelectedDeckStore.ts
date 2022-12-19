import {writable} from 'svelte/store';

const Store = localStorage.getItem("adminSelectedDeck");
export const adminSelectedDeckStore = writable(JSON.parse(Store) || null);
adminSelectedDeckStore.subscribe(value => {
    localStorage.setItem("adminSelectedDeck", JSON.stringify(value));
});