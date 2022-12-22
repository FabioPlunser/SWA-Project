import {writable} from 'svelte/store';

const Store = localStorage.getItem("userSelectedDeck");
export const userSelectedDeckStore = writable(JSON.parse(Store) || null);
userSelectedDeckStore.subscribe(value => {
    localStorage.setItem("userSelectedDeck", JSON.stringify(value));
});