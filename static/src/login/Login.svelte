<script lang="ts">
    // TODO implement login locig and page redirection
    import Nav from "../lib/components/nav.svelte";
    import {loggedIN} from "../lib/stores/loggedIn";
    let username = 'user';
    let password = 'user';
    function login(){
        if(username == 'user' && password == 'user'){
            loggedIN.set(true);
            window.location.href = '/';
        }
    }
</script>

<svelte:head>
	<link rel="icon" type="image/png" href="/favicon.png"/>
	<title>Login</title>
</svelte:head>


<main class="flex justify-center items-center mx-auto h-screen text-white">
    <div class="rounded-xl shadow-2xl bg-slate-900 max-w-fit p-10">
        <h1 class="underline text-2xl mx-auto flex justify-center p-2">Login</h1>
        <div class="form-control">
            <label class="input-group">
              <span>Username</span>
              <input type="text" bind:value={username} placeholder="Max" class="input input-bordered" />
            </label>
        </div>
        <br class="pt-4"/>
        <div class="form-control">
            <label class="input-group">
              <span>Password</span>
              <input type="text" bind:value={password} placeholder="1234" class="input input-bordered" />
            </label>
        </div>
        <div class="flex justify-between pt-4">
            <button on:click={login} class="btn btn-primary">Login</button>
            <button class="btn btn-primary">Register</button>
        </div>
    </div>
</main>
