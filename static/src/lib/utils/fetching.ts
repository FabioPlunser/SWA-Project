import { addToastByRes } from "./addToToastStore";
import { tokenStore } from "../stores/tokenStore";
import { get } from "svelte/store";


/**
 * a general fetching function for all fetch requests
 * @param url the url to fetch
 * @param method the method to use
 * @param data the data to send OPTIONAL
 * @param json if the data is json O
 * @param params the params to add to the url OPTIONAL
 * @returns the response
 * @example fetching('http://localhost:3000/api/v1/users', 'GET', null, false, [{limit: 10}, {offset: 0}])
 */
export async function fetching(url: string, method: string, params?: json[], data?, json?:boolean){
  let requestOptions;
  let myHeaders = new Headers();
  myHeaders.append("Authorization", "Bearer " + get(tokenStore));

  console.log("fetching", url, method, params, data, json)
  if(params){
    for(let param of params){
      url = url + '?' + Object.keys(param) + '=' + Object.values(param);
    }
  }
  
  if(method === 'GET'){
    requestOptions = {
      method: method,
      headers: myHeaders,
    };
  }
  else if(json && (method === 'POST' || method === 'PUT' || method === 'DELETE')){
    myHeaders.append("Content-Type", "application/json");
    requestOptions = {
      method: method,
      headers: myHeaders,
      body: JSON.stringify(data),
    };
  }
  else if(!json && (method === 'POST' || method === 'PUT' || method === 'DELETE')){
    requestOptions = {
      method: method,
      headers: myHeaders,
      body: data,
    };
  }

  console.log("requestOptions", requestOptions);
  let res = await fetch(url, requestOptions);
  res = await res.json();
  console.log(res);
  return res;
}

