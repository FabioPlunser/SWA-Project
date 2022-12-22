import {toast} from '../stores/toastStore'

export function addToast(msg, theme) {
    toast.push(msg, {theme: theme})
}
