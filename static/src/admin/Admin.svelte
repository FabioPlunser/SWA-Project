<script lang="ts">
	import { onMount } from 'svelte';
    import Nav from "../lib/components/nav.svelte";

    let users = [
        {
            username: "user",
            password: "user",
            email: "test@example.com"
        },
        {
            username: "admin",
            password: "admin",
            email: "test@example.com"
        }
    ];

    onMount(async () => {
        //TODO fetch users from backend
        // const response = await fetch("/api/users");
        // const data = await response.json();
        // users = data;
    });

    function deleateUser(){
        for (let i = 0; i < users.length; i++) {
            if (users[i].username == "user") {
                users = users.slice(i, 1);
                console.log("deleated user");
            }
        }
    }


</script>

<svelte:head>
	<link rel="icon" type="image/png" href="favicon.png"/>
	<title>Admin</title>
</svelte:head>

<Nav title="Admin"/>
<main class="m-20 flex-justify-center">
    <div class="flex justify-center">
        <button class="btn btn-primary">Create New User</button>
    </div>
    {#key users}
        {#each users as user}
            <div class="flex justify-center m-2">
                <div class="form-control mx-2">
                    <label class="input-group">
                      <span class="bg-slate-500">Username</span>
                      <input type="text" placeholder="info@site.com" class="input input-bordered bg-slate-900" bind:value={user.username} />
                    </label>
                </div>
                <div class="form-control mx-2">
                    <label class="input-group">
                      <span class="bg-slate-500">Password</span>
                      <input type="text" placeholder="info@site.com" class="input input-bordered bg-slate-900" bind:value={user.password} />
                    </label>
                </div>
                <div class="form-control mx-2">
                    <label class="input-group">
                      <span class="bg-slate-500">Email</span>
                      <input type="text" placeholder="info@site.com" class="input input-bordered bg-slate-900" bind:value={user.email} />
                    </label>
                </div>
                <button class="btn btn-secondary mx-2">Edit</button>
                <button class="btn btn-info mx-2" on:click={deleateUser}>Delete</button>
            </div>
        {/each} 
    {/key}


</main>
