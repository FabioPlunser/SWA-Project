export async function formFetch(e) {
  const action = e.target.action;
  const method = e.target.method.toUpperCase();

  console.log(action);
  console.log(method);

  if(method === 'GET') {
    let res = await fetch(action)
    res = await res.json();
    return res;
  }
  if(method === 'POST') {
    const formdata = new FormData(e.target);
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
