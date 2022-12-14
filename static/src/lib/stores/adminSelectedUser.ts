import {writable} from 'svelte/store';

const Store = localStorage.getItem("adminSelectedUser");
export const adminSelectedUser = writable(JSON.parse(Store) || null);
adminSelectedUser.subscribe(value => {
    localStorage.setItem("adminSelectedUser", JSON.stringify(value));
});