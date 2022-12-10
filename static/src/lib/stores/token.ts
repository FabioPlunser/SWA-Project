import { writable } from "svelte/store";

const Store = localStorage.getItem("token");
export const token = writable(Store || "");
token.subscribe(value => {
    localStorage.setItem("token", value);
});