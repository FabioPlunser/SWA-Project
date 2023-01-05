import { addToastByRes } from "./addToToastStore";
import { tokenStore } from "../stores/tokenStore";
import { get } from "svelte/store";

let token = get(tokenStore);
let myHeaders = new Headers();
myHeaders.append("Authorization", "Bearer " + token);

export async function formFetch(e, json?: boolean) {
    const action = e.target.action;
    const method = e.target.method.toUpperCase();
    let formData = new FormData(e.target);

    if(json) {
      myHeaders.append("Content-Type", "application/json");
      const data = {};
      for (let [key, value] of formData.entries()) {
        data[key] = value;
      }
      formData = JSON.stringify(data);
    }

    if(method === 'GET') {
      let requestOptions = {
        method: 'GET',
        headers: myHeaders,
      };
      let res = await fetch(action, requestOptions)
      res = await res.json();
      if(!res.success){
        addToastByRes(res);
      }
      return res;
    }
    
    if(method === 'POST') {
      let requestOptions = {
        method: 'POST',
        headers: myHeaders,
        body: formData,
      };
      let res = await fetch(action, requestOptions);
      res = await res.json();
      addToastByRes(res);
      return res;
    }
}

export async function fetching(url: string, method: string, params?: JSON[]){
  let requestOptions;
  let res;


  if(method === 'GET') {   
    requestOptions = {
      method: 'GET',
      headers: myHeaders,
    };
    if(params){
      for(let param of params){
        url = url + '?' + Object.keys(param) + '=' + Object.values(param);
      }
    }
    res = await fetch(url, requestOptions);
    res = await res.json();
    // addToastByRes(res);
    return res;
  }

  if(method === 'POST') {
    if(params){
      requestOptions = {
        method: 'POST',
        headers: myHeaders,
      };

      for(let param of params){
        url = url + '?' + Object.keys(param) + '=' + Object.values(param);
      }
    }else{
      requestOptions = {
        method: 'POST',
        headers: myHeaders,
        body: JSON.stringify(data),
      };
    }

    res = await fetch(url, requestOptions);
    res = await res.json();
    addToastByRes(res);
    return res;
  }
}
