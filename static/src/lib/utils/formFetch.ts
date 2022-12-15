export async function formFetch(e, token = "") {
  const action = e.target.action;
  const method = e.target.method.toUpperCase();
  

  if(method === 'GET') {
    var myHeaders = new Headers();
    myHeaders.append("Authorization", "Bearer " + token);

    var requestOptions = {
      method: 'GET',
      header: myHeaders,
    };
    let res = await fetch(action, requestOptions)
    return res = await res.json();
  }
  if(method === 'POST') {
    var myHeaders = new Headers();
    myHeaders.append("Authorization", "Bearer " + token);
    
    const formdata = new FormData(e.target);

    var requestOptions = {
      method: 'POST',
      header: myHeaders,
      body: formdata,
    };
    // for(let pair of formdata.entries()) {
    //   console.log(pair[0]+ ', '+ pair[1]);
    // }
    // for(let pair of myHeaders.entries()) {
    //   console.log(pair[0]+ ', '+ pair[1]);
    // }
    let res = await fetch(action, requestOptions);
    return res = await res.json();
  }
}
