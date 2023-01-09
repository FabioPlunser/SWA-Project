import { writable } from "svelte/store";

const Store = localStorage.getItem("jwt");
export const jwt = writable(Store || {username: "", token: ""});
jwt.subscribe(value => {
    localStorage.setItem("jwt", value);
});