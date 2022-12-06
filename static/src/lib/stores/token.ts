import { writable } from "svelte/store";

const Store = localStorage.getItem("token");
export const token = writable(Store || null);
token.subscribe(value => {
    localStorage.setItem("token", value);
});