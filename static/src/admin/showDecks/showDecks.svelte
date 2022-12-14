<script lang="ts">
  import favicon from "/favicon.png";
  import Nav from "../../lib/components/nav.svelte";
  import { redirect } from '../../lib/utils/redirect';
  import { handleLogout } from "../../lib/utils/handleLogout";
  import { token } from './../../lib/stores/token';
  import { adminSelectedUser } from './../../lib/stores/adminSelectedUser';
	import { adminSelectedDeck } from './../../lib/stores/adminSelectedDeck';
  import { onMount } from "svelte";

  let buttons = [
    { tag: "button", id: "", text: "Admin", action: () => redirect("admin") },
    { tag: "button", id: "", text: "Home", action: () => redirect("") },
    { tag: "button", id: "", text: "Logout",action: () => handleLogout()}
  ];

 
  $: selectedUser = $adminSelectedUser;
  onMount(async () => {
    await fetchDecks();
  });

  $: {
    if (!$token) {
      redirect("login");
    }
  }

  let decks = [
    {
      id: 1, 
      title: "Deck1",
      description: "This is a deck",
      blocked: false,
      cards: [
        { id: 1, front: "Deck1 Front1", back: "Deck1 Back1" },
        { id: 2, front: "Deck1 Front2", back: "Deck1 Back2" }
      ]
    },
    {
      id: 2, 
      title: "Deck2",
      description: "This is a deck",
      blocked: true,
      cards: [
        { id: 1, front: "Deck2 Front1", back: "Deck2 Back1" },
        { id: 2, front: "Deck2 Front2", back: "Deck2 Back2" }
      ]
    },
    {
      id: 3, 
      title: "Deck3",
      blocked: false,
      description: "This is a deck",
      cards: [
        { id: 1, front: "Deck3 Front1", back: "Deck3 Back1" },
        { id: 2, front: "Deck3 Front2", back: "Deck3 Back2" }
      ]
    }
  ]

  async function fetchDecks(){
    selectedUser = $adminSelectedUser;
    const myHeaders = new Headers();
    myHeaders.append("Authorization", "Bearer " + $token);
    myHeaders.append("Content-Type", "application/json");

    let res = await fetch("api/getUserDecks/personId " +  selectedUser.peronId, {
      method: "GET",
      headers: myHeaders,
    });
    res = await res.json(); 
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
    {#each decks as deck}
      {#if deck.blocked}
          <div class="card bg-slate-900 rounded-xl shadow-xl opacity-50">
            <div class="card-body">
              <h2 class="card-title">{deck.title}</h2>
              <p class="card-text">{deck.description}</p>
              <div class="card-actions">
                <button class="btn btn-primary" on:click={()=>deck.blocked = !deck.blocked}>Unblock</button>
                <button class="btn btn-primary" on:click={()=>{$adminSelectedDeck=deck; redirect("admin/showcards")}}>ShowCards</button>
              </div>
            </div>
        </div>
      {:else}
        <div class="card bg-slate-900 rounded-xl shadow-xl">
            <div class="card-body">
              <h2 class="card-title">{deck.title}</h2>
              <p class="card-text">{deck.description}</p>
              <div class="card-actions">
                <button class="btn btn-primary" on:click={()=>deck.blocked = !deck.blocked}>Block</button>
                <button class="btn btn-primary" on:click={()=>{$adminSelectedDeck=deck; redirect("admin/showcards")}}>ShowCards</button>
              </div>
            </div>
        </div>
      {/if}
    {/each}
  </div>
</main>