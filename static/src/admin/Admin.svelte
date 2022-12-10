<script lang="ts">
	import favicon  from '/favicon.png';
  import Nav from "../lib/components/nav.svelte";
  import Modal from "../lib/components/modal.svelte";
  import { formFetch } from '../lib/utils/formFetch';
  import { token } from '../lib/stores/token';
  import { get } from 'svelte/store';
  $: tokenValue = get(token);
  let users = [];
  
  $: getAllUser(); 
  
  async function getAllUser(){
    var myHeaders = new Headers();
    myHeaders.append("Authorization", "Bearer " + tokenValue);

    var requestOptions = {
      method: 'GET',
      headers: myHeaders,
    };

    let res = await fetch("api/getAllUsers", requestOptions)
    res = await res.json();
    users = res.items;
  }

  let showCreateModal = false;
  let showEditModal = false;

  let selectedUser = null;

  let searchUsername = "";
  let searchEmail = "";

  async function handleSubmit(e){
    const action = e.target.action;
    const method = e.target.method.toUpperCase();

    const myHeader = new Headers()
    myHeader.append("Authorization", "Bearer " + tokenValue);
    
    const formData = new FormData(e.target);

    var requestOptions = {
      method: method,
      headers: myHeader,
      body: formData
    };

    let res = await fetch(action, requestOptions);
    if(!res.success) alert(res.message);
    await getAllUser();
  }
  async function deleteUser(e){
    const action = e.target.action;
    const method = e.target.method.toUpperCase();

    const myHeader = new Headers()
    myHeader.append("Authorization", "Bearer " + tokenValue);
    console.log(e.target)
    const formData = new FormData(e.target);
    for(let pair of formData.entries()) {
      console.log(pair[0]+ ', '+ pair[1]);
    }
    var requestOptions = {
      method: method,
      headers: myHeader,
      body: formData
    };

  }

</script>

<svelte:head>
	<link rel="icon" type="image/png" href={favicon}/>
	<title>Admin</title>
    
</svelte:head>

<Nav title="Admin"/>
{#if showCreateModal}
    <Modal uniqueModalQualifier={"AdminCreateUser"}>
        <h1 class="flex justify-center underline text-2xl">Create User</h1>
        <br class="mt-20"/>
        <form id="AdminCreateUser" method='POST' action='api/createUser' on:submit|preventDefault={handleSubmit}>
            <div class="flex flex-col">
                <div class="form-control">
                    <label class="input-group">
                      <span class="w-36">Username</span>
                      <input name="username" required type="text" placeholder="Max" class="input input-bordered w-full" />
                    </label>
                </div>
                <br class="pt-4"/>
                <div class="form-control">
                    <label class="input-group">
                      <span class="w-36">Email</span>
                      <input name="email" required type="email" placeholder="test@example" class="flex input input-bordered w-full" />
                    </label>
                </div>
                <br class="pt-4"/>
                <div class="form-control">
                    <label class="input-group">
                      <span class="w-36">Password</span>
                      <input name="password" required type="password" placeholder="1234" class="input input-bordered w-full" />
                    </label>
                </div>
                <br class="pt-4"/>
                <div class="flex justify-center">
                  <span class="">Admin</span>
                  <input name="isAdmin" type="boolean" value="false" class="checkbox flex items-center m-2" />
                </div>
                <div class="flex justify-between mt-2">
                  <button type="submit" class="btn btn-primary">Register</button>
                  <label for="AdminCreateUser" class="btn btn-primary" on:click={()=> showCreateModal = !showCreateModal}>Close</label>
                </div>
            </div>
        </form>
    </Modal>
{/if}

{#if showEditModal}
    <Modal uniqueModalQualifier={"AdminEditUser"}>
        <h1 class="flex justify-center">Edit User</h1>
        <form id="EditUser" class="flex justify-center" method="POST" action="api/register" on:submit|preventDefault={handleSubmit}>
          <input class="hide" type="hidden" bind:value={selectedUser.person_id}>
          <div class="flex flex-col">
              <div class="form-control">
                  <label class="input-group">
                  <span class="w-36">Username</span>
                  <input bind:value={selectedUser.username} name="username" required type="text" placeholder="Max" class="input input-bordered w-full" />
                  </label>
              </div>
              <br class="pt-4"/>
              <div class="form-control">
                  <label class="input-group">
                  <span class="w-36">Email</span>
                  <input bind:value={selectedUser.email} name="email" required type="text" placeholder="test@example.com" class="input input-bordered w-full" />
                  </label>
              </div>
              <br class="pt-4"/>
              <div class="form-control">
                  <label class="input-group">
                  <span class="w-36">Password</span>
                  <input bind:value={selectedUser.password} name="password" required type="text" placeholder="1234" class="input input-bordered w-full" />
                  </label>
              </div>
              <button class="mt-10 btn btn-primary" type="submit">Update</button>
          </div>
        </form>
    </Modal>
{/if}


<main class="mt-20 m-2 flex-justify-center">
    <div class="flex justify-center">
        <label for="AdminCreateUser" class="btn btn-primary" on:click={()=> showCreateModal = true}>Create User</label>
    </div>
    <br class="mt-20"/>
    <!-- Table -->
    <!-- TODO add all decks of user and add the ability to block them -->
    <div class="overflow-x-auto z-0">
        <table class="table table-zebra table-compact w-full z-0">
          <thead class="z-0">
            <tr>
              <th>Search</th>
              <th><input bind:value={searchUsername} class="input bg-slate-900" placeholder="Username"/></th>
              <th><input bind:value={searchEmail} class="input bg-slate-900" placeholder="Email"/></th>
              <th></th>
              <th></th>
              <th></th>
            </tr>
          </thead>
          <thead class="bg-slate-900">
            <tr>
              <th>Id</th>
              <th>Username</th>
              <th>email</th>
              <th>password</th>
              <th>edit</th>
              <th>remove</th>
            </tr>
          </thead>
          <tbody>
            {#key users}
                {#each users as user}
                    {#if user.username.includes(searchUsername) && user.email.includes(searchEmail)}
                        <tr>
                            <td>{user.personId.slice(0,5)+"..."}</td>
                            <td>{user.username}</td>
                            <td>{user.email}</td>
                            <td class="hidetext">{user.password}</td>
                            <td><label class="btn btn-secondary" on:click={()=>{showEditModal=true; selectedUser=user}}>Edit</label></td>
                            <td><button class="btn btn-info" form="EditDeleteUser" type="submit">Delete</button></td>
                        </tr>
                    {/if}
                {/each}
              {/key}
          </tbody>
        </table>
      </div>
</main>

<style>
    .hidetext { -webkit-text-security: disc; /* Default */ }
</style>
