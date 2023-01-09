import { writable } from "svelte/store";

const Store = localStorage.getItem("jwt");
export const jwt = writable<{ token: string, username: string } | null>(JSON.parse(Store) || null);
jwt.subscribe((value: {token: string, username: string} | null) => {
    localStorage.setItem("jwt", JSON.stringify(value));
});