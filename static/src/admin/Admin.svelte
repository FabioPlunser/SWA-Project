<script lang="ts">
  import favicon  from '/favicon.png';
  import Nav from "../lib/components/nav.svelte";
  import Modal from "../lib/components/modal.svelte";
  import { token } from '../lib/stores/token';
  import { get } from 'svelte/store';
  import { redirect } from '../lib/utils/redirect';
  import { handleLogout } from '../lib/utils/handleLogout';
  import { onMount } from 'svelte';


  $: tokenValue = get(token);
  let users = [];
  let permissions = [];

  let showCreateModal = false;
  let showEditModal = false;
  
  let selectedUser = null;
  
  let searchUsername = "";
  let searchEmail = "";
  let searchPermission = "";
  let showDecks = false;
  let listCards = false;
  let selectedDeck = null;

  let buttons = [
    {
      tag: "button",
      id: "",
      text: "Home",
      action: () => redirect("")
    },
    {
      tag: "button",
      id: "",
      text: "Logout",
      action: () => handleLogout()
    }
  ];

  let decks = [
    {
      id: 1,
      title: "Deck1",
      description: "Deck1 Testing",
      cards: [
        {
          id: 1,
          front: "SOLID",
          back: "Hallo"
        },
        {
          id: 2,
          front: "Assembly",
          back: "kein bock"
        }
      ]
    },
    {
      id: 2,
      title: "Deck2",
      description: "Deck2 Testing",
      cards: [
        {
          id: 1,
          front: "Testing",
          back: "Hallo"
        }
      ]
    }
  ];


  
  $: getAllUser(); 
  $: getAllPermission();
  $: handleURLParams();

  /**
   * Handles the URL parameters and sets the appropriate variables
   * Used for showing decks and cards for a specific user 
   * Made for better UX when navigating between pages, because without it the back-button would not work as expected
   */
  function handleURLParams(){
    const urlParams = new URLSearchParams(window.location.search);
    if(urlParams.toString().length > 0){
      showDecks = urlParams.get("showDecks") === "true";
      listCards = urlParams.get("listCards") === "true";
      selectedUser = {
        personId: urlParams.get("userID"),
        username: urlParams.get("username")
      }
      selectedDeck = decks.find(deck => deck.id === parseInt(urlParams.get("deckID")));
    }
  }
  
  
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

  async function getAllPermission(){
    var myHeaders = new Headers();
    myHeaders.append("Authorization", "Bearer " + tokenValue);

    var requestOptions = {
      method: 'GET',
      headers: myHeaders,
    };

    let res = await fetch("api/getAllPermissions", requestOptions)
    res = await res.json();
    permissions = res.items;
    
  }
  


  $:{
    /**
     * If something happens on the page and the page should show decks of cards, fetch those form the backend
     */
    async function getFromServer(){
      if(showDecks || listCards){
        //fetch decks from backend 
        console.log("fetch decks");
        let res = await fetch("api/getAllDecks?userID=" + selectedUser.personId, {
          method: "GET",
          headers: {
            "Authorization": "Bearer " + tokenValue
          }
        })
        res = await res.json();
        // decks = res.items;
        console.log(res);
      }
    }

    getFromServer();
  }



  async function handleSubmit(e){
    const action = e.target.action;
    const method = e.target.method.toUpperCase();
    const myHeader = new Headers()
    myHeader.append("Authorization", "Bearer " + tokenValue);
    //TODO form validation
    const formData = new FormData(e.target);
    // for(let [key, value] of formData.entries()){
      //   console.log(key, value)
      // }
      
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



  function handleShowDecks(){
    redirect(`admin?showDecks=${true}&userID=${selectedUser.personId}&username=${selectedUser.username}`);
  }

  function handleListCards(){
    redirect(`admin?listCards=${true}&deckID=${selectedDeck.id}&deckTitle=${selectedDeck.title}`);
  }

</script>

<svelte:head>
	<link rel="icon" type="image/png" href={favicon}/>
	<title>Admin</title>
    
</svelte:head>

<Nav title="Admin" buttons={buttons}/>
{#if showCreateModal}
    <Modal uniqueModalQualifier={"AdminCreateUser"}>
        <h1 class="flex justify-center underline text-2xl">Create User</h1>
        <br class="pt-4"/>
        <form method='POST' action='api/createUser' on:submit|preventDefault={handleSubmit}>
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
                  <!-- svelte-ignore a11y-click-events-have-key-events -->
                <label for="AdminCreateUser" class="btn btn-primary" on:click={()=> showCreateModal = !showCreateModal}>Close</label>
              </div>
            </div>
        </form>
    </Modal>
{/if}

{#if showEditModal}
  <Modal uniqueModalQualifier={"AdminEditUser"}>
      <h1 class="flex justify-center">Edit User</h1>
      <br class="pt-4"/>
      <form class="flex justify-center" method="POST" action="api/updateUser" on:submit|preventDefault={handleSubmit}>
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

              <!-- svelte-ignore a11y-click-events-have-key-events -->
            <label for="AdminEditUser" class="btn btn-primary" on:click={()=> showCreateModal = !showCreateModal}>Close</label>
          </div>
        </div>
      </form>
  </Modal>
{/if}

{#if !showDecks && !listCards}
<main class="mt-20 m-2 flex-justify-center">
  <div class="flex justify-center">
      <!-- svelte-ignore a11y-click-events-have-key-events -->
      <label for="AdminCreateUser" class="btn btn-primary" on:click={()=> showCreateModal = true}>Create User</label>
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
            <th>edit</th>
            <th>remove</th>
          </tr>
        </thead>
        <tbody>
          {#key users}
              {#each users as user}
                  {#if user.username.toLowerCase().includes(searchUsername.toLowerCase()) && user.email.toLowerCase().includes(searchEmail.toLowerCase()) && user.permissions.toString().toLowerCase().includes(searchPermission.toLowerCase())}
                        <tr>
                          <td><form action="api/deleteUser" method="POST" on:submit|preventDefault={handleSubmit} id={user.personId} name="personId"/>{user.personId.slice(0,5)+"..."}</td>
                          <input type="hidden" form={user.personId} bind:value={user.personId} name="personId"/>
                          <td><input form={user.personId} type="text" name="username" bind:value={user.username} class="bg-transparent" readonly/></td>
                          <td><input form={user.personId} type="text" name="email" bind:value={user.email} class="bg-transparent" readonly/></td>
                          <td><input form={user.personId} type="text" name="permissions" bind:value={user.permissions} class="bg-transparent" readonly/></td>
                          <td><div class="tooltip" data-tip="Show all decks of user"><button class="btn btn-info" on:click={()=>{selectedUser=user; handleShowDecks();}}>Decks</button></div></td>
                          <!-- svelte-ignore a11y-click-events-have-key-events -->
                          <td><label for="AdminEditUser" class="btn btn-secondary" on:click={()=>{showEditModal=true; selectedUser=user}}>Edit</label></td>
                          <td><button class="btn btn-info" form={user.personId} type="submit">Delete</button></td>
                      </tr>
                  {/if}
              {/each}
            {/key}
        </tbody>
      </table>
    </div>
</main>

{/if}

{#if showDecks && !listCards}
<main class="mt-20 m-2 flex-justify-center">
  <h1 class="text-2xl flex justify-center">Decks of {selectedUser.username}</h1>
  <br/>
  <!-- <div class="grid grid-cols-4 gap-4"> {#each decks as deck} <AdminDeck {...deck} on:listCards={()=>{selectedDeck=deck; handleListCards();}}/> {/each} </div> -->
</main>
{/if}


{#if listCards && !showDecks}
<main class="mt-20 m-2 flex-justify-center">
  <h1 class="text-2xl flex justify-center">Cards of {selectedDeck.title}</h1>
  <br/>
  <div class="grid grid-cols-4 gap-4">
    {#each selectedDeck.cards as card}
      <div class="card rounded-2xl bg-slate-900 shadow-xl">
        <div class="card-body">
          <div class="flex flex-col w-full lg:flex-row">
            <div>Front</div>
            <div class="grid flex-grow h-32 card bg-base-300 rounded-box place-items-center">{card.front}</div> 
            <div class="divider lg:divider-horizontal"></div> 
            <div>Back</div>
            <div class="grid flex-grow h-32 card bg-base-300 rounded-box place-items-center">{card.back}</div>
          </div>
        </div>
      </div>
    {/each}
  </div>
</main>

{/if}