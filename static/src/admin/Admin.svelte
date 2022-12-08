<script lang="ts">
	import { onMount } from 'svelte';
    import Nav from "../lib/components/nav.svelte";
    import Modal from "../lib/components/modal.svelte";

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

    let showCreateModal = false;
    let showEditModal = false;

    let selectedUser = null;

    let searchFirstName = "";
    let searchLastName = "";
    let searchEmail = "";
    let data = [
        {id: 1, firstName: "Max", lastName: "Mustermann", email: "test@example.com"},
        {id: 2, firstName: "David", lastName: "Mustermann", email: "test@example.com"},
        {id: 3, firstName: "Fabio", lastName: "Mustermann", email: "test@example.com"},
        {id: 4, firstName: "Lukas", lastName: "Mustermann", email: "test@example.com"},
        {id: 5, firstName: "Fabian", lastName: "Mustermann", email: "test@example.com"},
        {id: 6, firstName: "Luana", lastName: "Mustermann", email: "test@example.com"},
    ]
</script>

<svelte:head>
	<link rel="icon" type="image/png" href="favicon.png"/>
	<title>Admin</title>
    
</svelte:head>

<Nav title="Admin"/>
{#if showCreateModal}
    <Modal uniqueModalQualifier={"AdminCreateUser"}>
        <h1 class="flex justify-center">Create User</h1>
        <form class="flex justify-center">
            <div class="flex justify-center flex-col mx-auto">
                <div class="form-control m-2">
                    <label class="input-group">
                      <span class="bg-slate-500">Username</span>
                      <input type="text" placeholder="info@site.com" class="input input-bordered bg-slate-900" id="username"/>
                    </label>
                </div>
                <div class="form-control m-2 min-w-fit">
                    <label class="input-group min-w-fit">
                      <span class="bg-slate-500">Email</span>
                      <input type="text" placeholder="info@site.com" class="input input-bordered bg-slate-900 min-w-fit" id="email"/>
                    </label>
                </div>
                <div class="form-control m-2">
                    <label class="input-group">
                      <span class="bg-slate-500">Password</span>
                      <input type="text" placeholder="info@site.com" class="input input-bordered bg-slate-900" id="password"/>
                    </label>
                </div>
            </div>

        </form>
    </Modal>
{/if}

{#if showEditModal}
    <Modal uniqueModalQualifier={"AdminEditUser"}>
        <h1 class="flex justify-center">Edit User</h1>
        <form class="flex justify-center">
            <div class="flex justify-center flex-col mx-auto">
                <div class="form-control m-2">
                    <label class="input-group">
                      <span class="bg-slate-500">Username</span>
                        <input type="text" bind:value={selectedUser.username} class="input input-bordered bg-slate-900 min-w-fit" id="username"/>
                    </label>
                </div>
                <div class="form-control m-2 min-w-fit">
                    <label class="input-group min-w-fit">
                      <span class="bg-slate-500">Email</span>
                      <input type="text" bind:value={selectedUser.mail} class="input input-bordered bg-slate-900 min-w-fit" id="email"/>
                    </label>
                </div>
                <div class="form-control m-2">
                    <label class="input-group">
                      <span class="bg-slate-500">Password</span>
                      <input type="text" bind:value={selectedUser.password} class="input input-bordered bg-slate-900 min-w-fit" id="password"/>
                    </label>
                </div>
            </div>
            </form>
    </Modal>
{/if}


<main class="m-20 flex-justify-center">
    <div class="flex justify-center">
        <label for="AdminCreateUser" class="btn btn-primary" on:click={()=> showCreateModal = true}>Create User</label>
    </div>
    <div class="overflow-x-auto">
        <table class="table table-zebra table-compact w-full">
          <!-- head -->
          <thead class="">
            <tr>
              <th>Search</th>
              <th><input bind:value={searchFirstName} class="input" placeholder="Max"/></th>
              <th><input bind:value={searchLastName} class="input" placeholder="Max"/></th>
              <th><input bind:value={searchEmail} class="input" placeholder="Max"/></th>
              <th></th>
              <th></th>
            </tr>
          </thead>
          <thead class="bg-slate-900">
            <tr>
              <th></th>
              <th>FirstName</th>
              <th>LastName</th>
              <th>email</th>
              <th>edit</th>
              <th>remove</th>
            </tr>
          </thead>
          <tbody>
            {#each data as user}
                {#if user.firstName.includes(searchFirstName) && user.lastName.includes(searchLastName) && user.email.includes(searchEmail)}
                    <tr>
                        <th>{user.id}</th>
                        <th>{user.firstName}</th>
                        <th>{user.lastName}</th>
                        <th>{user.email}</th>
                        <th><button class="btn btn-secondary">Edit</button></th>
                        <th><button class="btn btn-info">Delete</button></th>
                    </tr>
                {/if}
            {/each}
          </tbody>
        </table>
      </div>
    <!-- {#key users}
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
                <label for="AdminEditUser" class="btn btn-secondary" on:click={()=>{showEditModal = true; selectedUser=user}}>Edit</label>

                <button class="btn btn-info mx-2" on:click={deleateUser}>Delete</button>
            </div>
        {/each} 
    {/key} -->


</main>
