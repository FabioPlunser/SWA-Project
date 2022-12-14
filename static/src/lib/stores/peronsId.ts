import { writable } from "svelte/store";

const Store = localStorage.getItem("personId");
export const personId = writable(Store || "");
personId.subscribe(value => {
    localStorage.setItem("personId", value);
});