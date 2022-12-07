export function redirect (pageName: string){
    window.location.replace(window.location.origin + `/${pageName}`);
}