<script lang="ts">
    // TODO implement login locig and page redirection
    import Nav from "../lib/components/nav.svelte";
    import {loggedIN} from "../lib/stores/loggedIn";

    let username = "";
    async function handleSubmit (e){
		// getting the action url
		const formdata = new FormData(e.target);

        var requestOptions = {
            method: 'POST',
            header: {
                'Content-Type': 'application/json',
                'Access-Control-Allow-Origin': '*'
            },
            body: formdata,
        };
        fetch("/api/register", requestOptions)
        .then(response => response.text())
        .then(result => console.log(result))
        .catch(error => console.log('error', error))
	}
</script>

<svelte:head>
	<link rel="icon" type="image/png" href="../static/882998.png"/>
	<title>Login</title>
</svelte:head>


<main class="flex justify-center items-center mx-auto h-screen text-white">
    <div class="rounded-xl shadow-2xl bg-slate-900 max-w-fit p-10">
        <h1 class="underline text-2xl mx-auto flex justify-center p-2">Login</h1>
        <form method="POST" action="api/register" on:submit|preventDefault={handleSubmit}>
            <div class="form-control">
                <label class="input-group">
                  <span>Username</span>
                  <input name="username" required type="text" placeholder="Max" class="input input-bordered" />
                </label>
            </div>
            <br class="pt-4"/>
            <div class="form-control">
                <label class="input-group">
                  <span>Email</span>
                  <input name="email" required type="text" placeholder="test@example" class="input input-bordered" />
                </label>
            </div>
            <br class="pt-4"/>
            <div class="form-control">
                <label class="input-group">
                  <span>Password</span>
                  <input name="password" required type="text" placeholder="1234" class="input input-bordered" />
                </label>
            </div>
            <div class="flex justify-between pt-4">
                <button type="submit" class="btn btn-primary">Login</button>
                <!-- <button class="btn btn-primary">Register</button> -->
            </div>
        </form>
    </div>
</main>
