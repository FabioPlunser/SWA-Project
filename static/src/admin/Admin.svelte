<script lang="ts">

  import favicon  from '/favicon.png';
  import Nav from "../lib/components/nav.svelte";
  import Modal from "../lib/components/modal.svelte";
  import { redirect } from '../lib/utils/redirect';
  import { handleLogout } from '../lib/utils/handleLogout';
  
  import { get } from 'svelte/store';
  import { tokenStore } from '../lib/stores/tokenStore';
	import { adminSelectedUserStore} from '../lib/stores/adminSelectedUserStore';
  import Spinner from '../lib/components/Spinner.svelte';
  import { addToastByRes } from '../lib/utils/addToToastStore';
  
  $: tokenValue = get(tokenStore);
  $: $adminSelectedUserStore = selectedUser;

  let users = [];
  let permissions = [];

  let showCreateModal = false;
  let showEditModal = false;
  
  let selectedUser = null;
  
  let searchUsername = "";
  let searchEmail = "";
  let searchPermission = "";

  let buttons = [
    { text: "Home", action: () => redirect("") },
    { text: "Admin",action: () => redirect("admin")},
    { text: "Logout",action: () => handleLogout()}
  ];


  $: getAllUser(); 
  $: getAllPermission();

  async function getAllUser(){
    var myHeaders = new Headers();
    myHeaders.append("Authorization", "Bearer " + tokenValue);

    var requestOptions = {
      method: 'GET',
      headers: myHeaders,
    };

    let res = await fetch("api/get-all-users", requestOptions)
    res = await res.json();
    if(res.success) users = res.items;
    else addToastByRes(res);
  }

  async function getAllPermission(){
    var myHeaders = new Headers();
    myHeaders.append("Authorization", "Bearer " + tokenValue);

    var requestOptions = {
      method: 'GET',
      headers: myHeaders,
    };

    let res = await fetch("api/get-all-permissions", requestOptions)
    res = await res.json();
    if(res.success) permissions = res.items;
    else addToastByRes(res);
    
  }

  async function handleSubmit(e){
    const action = e.target.action;
    const method = e.target.method.toUpperCase();
    const myHeader = new Headers()
    myHeader.append("Authorization", "Bearer " + tokenValue);
    //TODO form validation
    const formData = new FormData(e.target);
      
    if(action.includes("delete") && formData.get("permissions").toString().includes("ADMIN")){
      if(!confirm(`Are you sure you want to delete ${formData.get("username").toString()}?`)) return;
    }
    if(action.includes("create")){
      showCreateModal = !showCreateModal;
    } 
    if(action.includes("update")){
      for(let [key, value] of formData.entries()){
        if(value === ''){
          formData.delete(key);
        }
      }
      showEditModal = !showEditModal;
    } 
    

    var requestOptions = {
      method: method,
      headers: myHeader,
      body: formData
    };

    try {
      let res = await fetch(action, requestOptions);
      res = await res.json();
      if(res.success === false) alert(res.message);
    } catch (error) {
      alert(action + " failed");
    }
    
    
    await getAllUser();

  }

</script>

<svelte:head>
	<link rel="icon" type="image/png" href={favicon}/>
	<title>Admin</title>
    
</svelte:head>

<Nav title="Admin" {buttons}/>
{#if showCreateModal}
    <Modal open={showCreateModal} on:close={()=>showCreateModal=false} closeOnBodyClick={false}>
        <h1 class="flex justify-center underline text-2xl">Create User</h1>
        <br class="pt-4"/>
        <form method='POST' action='api/create-user' on:submit|preventDefault={handleSubmit}>
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
              <div class="form-control">
                <label class="input-group min-h-fit">
                  <span class="w-36 min-h-fit">Admin</span>
                  <select multiple name="permissions" class="flex input w-full" required>
                    {#each permissions as permission}
                      {#if permission === "USER"}
                        <option selected>{permission}</option>
                      {:else}
                        <option>{permission}</option>
                      {/if}
                    {/each}
                  </select>
                </label>
              </div>
              <br class="pt-4"/>
              <div class="flex justify-between">
                <button type="submit" class="btn btn-primary">Register</button>
                <input type="reset" class="btn btn-primary" value="Clear"/>
                <button class="btn btn-primary" on:click={()=> showCreateModal = false}>Close</button>
              </div>
            </div>
        </form>
    </Modal>
{/if}

{#if showEditModal}
  <Modal open={showEditModal} on:close={()=>showEditModal=false} closeOnBodyClick={false}>
      <h1 class="flex justify-center">Edit User</h1>
      <br class="pt-4"/>
      <form class="flex justify-center" method="POST" action="api/update-user" on:submit|preventDefault={handleSubmit}>
        <input name="personId" type="hidden" bind:value={selectedUser.personId} required>
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
              <input name="password" class="input input-bordered w-full" type="password">
            </label>
          </div>
          <br class="pt-4"/>
          <div class="form-control">
            <label class="input-group">
              <span class="w-36">Admin</span>
              <select multiple name="permissions" class="flex input w-full" required>
                {#each permissions as permission}
                  {#if selectedUser.permissions.includes(permission)}
                    <option selected>{permission}</option>
                  {:else}
                    <option>{permission}</option>
                  {/if}
                {/each}
              </select>
            </label>
          </div>
          <br class="pt-4"/>
          <div class="flex justify-between">
            <button type="submit" class="btn btn-primary">Update</button>
            <input type="reset" class="btn btn-primary" value="Clear"/>
            <button class="btn btn-primary" on:click={()=> showEditModal = false}>Close</button>
          </div>
        </div>
      </form>
  </Modal>
{/if}

<main class="mt-20 m-2 flex-justify-center">
  <div class="flex justify-center">
      <button class="btn btn-primary" on:click={()=> showCreateModal = true}>Create User</button>
  </div>
  <br class="mt-20"/>
  <!-- TODO add all decks of user and add the ability to block them -->
  <div class="overflow-x-auto z-0">
      <table class="table table-zebra table-compact w-full z-0">
        <thead class="z-0">
          <tr>
            <th>Search</th>
            <th><input bind:value={searchUsername} class="input bg-slate-900" placeholder="Username"/></th>
            <th><input bind:value={searchEmail} class="input bg-slate-900" placeholder="Email"/></th>
            <th><input bind:value={searchPermission} class="input bg-slate-900" placeholder="ADMIN"/></th>
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
            <th>Roles</th>
            <th>showDecks</th>
            <th>edit</th>
            <th>remove</th>
          </tr>
        </thead>
        <tbody>
          {#if users.length === 0}
            <Spinner/>
          {:else}
          {#key users}
              {#each users as user}
                  {#if user.username.toLowerCase().includes(searchUsername.toLowerCase()) && user.email.toLowerCase().includes(searchEmail.toLowerCase()) && user.permissions.toString().toLowerCase().includes(searchPermission.toLowerCase())}
                        <tr>
                          <td><form action="api/deleteUser" method="POST" on:submit|preventDefault={handleSubmit} id={user.personId} name="personId"/>{user.personId.slice(0,5)+"..."}</td>
                          <input type="hidden" form={user.personId} bind:value={user.personId} name="personId"/>
                          <td><input form={user.personId} type="text" name="username" bind:value={user.username} class="bg-transparent" readonly/></td>
                          <td><input form={user.personId} type="text" name="email" bind:value={user.email} class="bg-transparent" readonly/></td>
                          <td><input form={user.personId} type="text" name="permissions" bind:value={user.permissions} class="bg-transparent" readonly/></td>
                          <td><button class="btn btn-info" on:click={()=>{$adminSelectedUserStore=user; redirect("admin/show-decks")}}>Decks</button></td>
                          <!-- svelte-ignore a11y-click-events-have-key-events -->
                          <td><button class="btn btn-secondary" on:click={()=>{showEditModal=true; selectedUser=user}}>Edit</button></td>
                          <td><button class="btn btn-info" form={user.personId} type="submit">Delete</button></td>
                      </tr>
                  {/if}
              {/each}
            {/key}
          {/if}
        </tbody>
      </table>
    </div>
</main>
