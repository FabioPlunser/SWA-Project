import { writable } from "svelte/store";

const Store = localStorage.getItem("jwt");
export const jwt = writable(Store || {username: null, token: null});
jwt.subscribe(value => {
    localStorage.setItem("jwt", value);
});