import { writable } from "svelte/store";

const Store = localStorage.getItem("token");
export const tokenStore = writable(Store || "");
tokenStore.subscribe(value => {
    localStorage.setItem("token", value);
});