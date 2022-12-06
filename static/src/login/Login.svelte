<script lang="ts">
    // TODO implement login locig and page redirection
    import favicon from "/favicon.png";
    import { redirect } from "../lib/utils/redirect";

    let token = "";
    async function handleSubmit (e){
		// getting the action url
		const formdata = new FormData(e.target);

        var requestOptions = {
            method: 'POST',
            header: {
                'Content-Type': 'application/json',
                'Access-Control-Allow-Origin': '*',
                'Access-Control-Allow-Headers': 'Content-Type, Authorization',
                'Access-Control-Allow-Methods': 'GET, POST, PUT, DELETE, OPTIONS',
            },
            body: formdata,
        };
        fetch("/api/login", requestOptions)
        .then(response => response.json())
        .then(result => {console.log(result); token = result.token})
        .catch(error => console.log('error', error))
        
	}
    $: console.log("token: " + token);
    
    
    async function getAllUsers(){
        var requestOptions = {
            method: 'POST',
            header: {
                'Content-Type': 'application/json',
                'Access-Control-Allow-Origin': '*',
                'Access-Control-Allow-Headers': 'Content-Type, Authorization',
                'Access-Control-Allow-Methods': 'GET, POST, PUT, DELETE, OPTIONS',
                'Authorization': `Bearer ${token}`
            },
        };
        fetch("/api/getAllUsers", requestOptions)
        .then(response => response.json())
        .then(result => console.log(result))
        .catch(error => console.log('error', error))
    } 
    // let token = "ed899ec7-81d5-4a6f-95d8-4b102e5d3173";
</script>

<svelte:head>
	<link rel="icon" type="image/png" href={favicon}/>
	<title>Login</title>
</svelte:head>


<main class="flex justify-center items-center mx-auto h-screen text-white">
    <div class="rounded-xl shadow-2xl bg-slate-900 max-w-fit p-10">
        <h1 class="underline text-2xl mx-auto flex justify-center p-2">Login</h1>
        <form method="POST" action="/api/login" on:submit|preventDefault={handleSubmit}>
            <div class="form-control">
                <label class="input-group">
                  <span>Username</span>
                  <input name="username" required type="text" placeholder="Max" class="input input-bordered" />
                </label>
            </div>
            <br class="pt-4"/>
            <br class="pt-4"/>
            <div class="form-control">
                <label class="input-group">
                  <span>Password</span>
                  <input name="password" required type="text" placeholder="1234" class="input input-bordered" />
                </label>
            </div>
            <div class="flex justify-between pt-4">
                <button type="submit" class="btn btn-primary">Login</button>
                <button class="btn btn-primary" on:click={()=>redirect("register")}>Register</button>
            </div>
        </form>
    </div>

    <button class="btn" on:click={getAllUsers}></button>
</main>
