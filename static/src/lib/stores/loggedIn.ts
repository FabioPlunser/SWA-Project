import { writable } from "svelte/store";

const Store = localStorage.getItem("loggedIN");
export const loggedIN = writable(Store || false);
loggedIN.subscribe(value => {
    console.log("loggedIN", value);
    localStorage.setItem("loggedIN", value === false ? false : true);
});