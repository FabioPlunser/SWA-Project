import { writable } from "svelte/store";

const Store = localStorage.getItem("userPermissions");
export const userPermissions = writable(Store || []);
userPermissions.subscribe(value => {
    localStorage.setItem("userPermissions", value);
});