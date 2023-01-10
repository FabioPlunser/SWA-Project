<script lang="ts">
  import favicon  from '../../assets/favicon.png';
  import Nav from "../../lib/components/nav.svelte";
  import Spinner from "../../lib/components/Spinner.svelte";
	import SvelteToast from './../../lib/components/SvelteToast.svelte';

  import { redirect } from '../../lib/utils/redirect';
  import { handleLogout } from "../../lib/utils/handleLogout";
  import { adminSelectedUserStore } from '../../lib/stores/adminSelectedUserStore';
	import { adminSelectedDeckStore } from '../../lib/stores/adminSelectedDeckStore';
  import { addToastByRes } from "../../lib/utils/addToToastStore";
  import { fetching } from "../../lib/utils/fetching";

  $: selectedUser = $adminSelectedUserStore;
  $: fetchDecks();

  let buttons = [
    { text: "Admin", action: () => redirect("admin") },
    { text: "Home", action: () => redirect("") },
    { text: "Logout",action: () => handleLogout()}
  ];
  
  async function fetchDecks(){
    selectedUser = $adminSelectedUserStore;
    let res = await fetching("/api/get-given-user-decks", "GET", [{name:"personId", value: selectedUser.personId}]);
    if(!res.success){
      return;
    }
    decks = res.items;
  }

  let decks = fetchDecks();

  async function blockDeck(deck){
    let res = await fetching("/api/block-deck", "POST", [{name:"deckId", value: deck.deckId}]);
    if(res.success) fetchDecks();
  }

  async function unblockDeck(deck){
    let res = await fetching("/api/unblock-deck", "POST", [{name:"deckId", value: deck.deckId}]);
    if(res.success) fetchDecks();
  }


</script>

<svelte:head>
  <title>Admin ShowDecks</title>
  <link rel="icon" type="image/png" href={favicon} />
  <script src="http://localhost:35729/livereload.js"></script>
</svelte:head>


<Nav title="ShowDecks" {buttons}/>
<SvelteToast/>
<main class="mt-20 m-2">
  <h1 class="flex justify-center text-2xl font-bold">Decks of {selectedUser.username}</h1>
  <br/>
    {#await decks}
      <Spinner/>
    {:then decks}
      {#if decks.length === 0}
        <h1 class="text-xl font-bold flex justify-center">User has no Decks</h1>
      {:else}
        {#key decks}
          <div class="grid grid-cols-4 gap-4">
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
          </div>
        {/key}
      {/if}
    {/await}
</main>