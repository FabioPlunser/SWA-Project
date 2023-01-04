<script lang="ts">
  import favicon from "/favicon.png";
  import Nav from "../../lib/components/nav.svelte";
  import { redirect } from '../../lib/utils/redirect';
  import { handleLogout } from "../../lib/utils/handleLogout";
  import { tokenStore } from '../../lib/stores/tokenStore';
  import { adminSelectedUserStore } from '../../lib/stores/adminSelectedUserStore';
	import { adminSelectedDeckStore } from '../../lib/stores/adminSelectedDeckStore';
  import { onMount } from "svelte";
  import { addToastByRes } from "../../lib/utils/addToToastStore";
  import Spinner from "../../lib/components/Spinner.svelte";

  $: selectedUser = $adminSelectedUserStore;
  $: {
    if (!$tokenStore) {
      redirect("login");
    }
  }
  $: fetchDecks();



  let buttons = [
    { text: "Admin", action: () => redirect("admin") },
    { text: "Home", action: () => redirect("") },
    { text: "Logout",action: () => handleLogout()}
  ];
  
  let decks = [];

  const myHeaders = new Headers();
  myHeaders.append("Authorization", "Bearer " + $tokenStore);

  async function fetchDecks(){
    selectedUser = $adminSelectedUserStore;
  
    let res = await fetch(`/api/get-given-user-decks?personId=${selectedUser.personId} `, {
      method: "GET",
      headers: myHeaders,
    });
    res = await res.json(); 
    if(res.success) decks = res.items;
    else addToastByRes(res);
  }


  async function blockDeck(deck){
    let res = await fetch(`/api/block-deck?deckId=${deck.deckId} `, {
      method: "POST",
      headers: myHeaders,
    });
    res = await res.json(); 
    if(res.success) fetchDecks();
    else{
      addToastByRes(res);
      
    } 
  }

  async function unblockDeck(deck){
    let res = await fetch(`/api/unblock-deck?deckId=${deck.deckId} `, {
      method: "POST",
      headers: myHeaders,
    });
    res = await res.json(); 
    if(res.success) fetchDecks();
    else{
      addToastByRes(res);
    } 
  }
</script>

<svelte:head>
  <title>Admin ShowDecks</title>
  <link rel="icon" type="image/png" href={favicon} />
</svelte:head>

<Nav title="ShowDecks" {buttons}/>
<main class="mt-20 m-2">
  <h1 class="flex justify-center text-2xl underline">Decks of {selectedUser.username}</h1>
  <br/>
  <div class="grid grid-cols-4 gap-4">
    {#if decks.length === 0}
      <Spinner/>
    {:else}
      {#key decks}
        {#each decks as deck}
          {#if deck.blocked}
              <div class="card bg-slate-900 rounded-xl shadow-xl opacity-50">
                <div class="card-body">
                  <h2 class="card-title">{deck.name}</h2>
                  <p class="card-text">{deck.description}</p>
                  <div class="card-actions">
                    <button class="btn btn-primary" on:click={()=>unblockDeck(deck)}>Unblock</button>
                    <button class="btn btn-primary" on:click={()=>{$adminSelectedDeckStore=deck; redirect("admin/show-cards")}}>ShowCards</button>
                  </div>
                </div>
            </div>
          {:else}
            <div class="card bg-slate-900 rounded-xl shadow-xl">
                <div class="card-body">
                  <h2 class="card-title">{deck.name}</h2>
                  <p class="card-text">{deck.description}</p>
                  <div class="card-actions">
                    <button class="btn btn-primary" on:click={()=>blockDeck(deck)}>Block</button>
                    <button class="btn btn-primary" on:click={()=>{$adminSelectedDeckStore=deck; redirect("admin/show-cards")}}>ShowCards</button>
                  </div>
                </div>
            </div>
          {/if}
        {/each}
      {/key}
    {/if}
   

  </div>
</main>