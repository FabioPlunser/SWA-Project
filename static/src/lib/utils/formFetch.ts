export async function formFetch(e) {
  const action = e.target.action;
  const method = e.target.method.toUpperCase();

  if(method === 'GET') {
    let res = await fetch(action)
    res = await res.json();
    return res;
  }
  if(method === 'POST') {
    const formdata = new FormData(e.target);
    console.log(formdata);
    for(var pair of formdata.entries()) {
      console.log(pair[0]+ ', '+ pair[1]);
    }
    var requestOptions = {
      method: e.target.method.toUpperCase(),
      header: {
          'Content-Type': 'application/json',
          'Access-Control-Allow-Origin': '*'
      },
      body: formdata,
    };
    let res = await fetch(action, requestOptions);
    res = await res.json();
    return res;
  }
}
