import { addToastByRes } from "./addToToastStore";
import { tokenStore } from "../stores/tokenStore";
import { get } from "svelte/store";


export async function formFetch(e, json?: boolean) {
  const action = e.target.action;
  const method = e.target.method.toUpperCase();
  let formData = new FormData(e.target);


  var myHeaders = new Headers();
  myHeaders.append("Authorization", "Bearer " + get(tokenStore));

  if(json) {
    const data = {};
    for (let [key, value] of formData.entries()) {
      data[key] = value;
    }
    formData = JSON.stringify(data);
  }

  if(method === 'GET') {
    var requestOptions = {
      method: 'GET',
      header: myHeaders,
    };
    let res = await fetch(action, requestOptions)
    res = await res.json();
    if(!res.success){
      addToastByRes(res);
    }
    return res;
  }
  
  if(method === 'POST') {
    var requestOptions = {
      method: 'POST',
      header: myHeaders,
      body: formData,
    };
    let res = await fetch(action, requestOptions);
    res = await res.json();
    addToastByRes(res);
    return res;
  }
}
