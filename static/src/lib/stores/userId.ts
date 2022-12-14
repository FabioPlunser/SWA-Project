import { writable } from "svelte/store";

const Store = localStorage.getItem("userId");
export const userId = writable(Store || "");
userId.subscribe(value => {
    localStorage.setItem("userId", value);
});