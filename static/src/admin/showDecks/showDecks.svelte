<script lang="ts">
  import favicon from "/favicon.png";
  import Nav from "../../lib/components/nav.svelte";
  import { redirect } from '../../lib/utils/redirect';
  import { handleLogout } from "../../lib/utils/handleLogout";
  import { tokenStore } from '../../lib/stores/tokenStore';
  import { adminSelectedUserStore } from '../../lib/stores/adminSelectedUserStore';
	import { adminSelectedDeckStore } from '../../lib/stores/adminSelectedDeckStore';
  import { onMount } from "svelte";

  let buttons = [
    { text: "Admin", action: () => redirect("admin") },
    { text: "Home", action: () => redirect("") },
    { text: "Logout",action: () => handleLogout()}
  ];

 
  $: selectedUser = $adminSelectedUserStore;
  onMount(async () => {
    await fetchDecks();
  });

  $: {
    if (!$tokenStore) {
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
        { id: 1, question: "Deck1 question1", answer: "Deck1 answer1" },
        { id: 2, question: "Deck1 question2", answer: "Deck1 answer2" }
      ]
    },
    {
      id: 2, 
      title: "Deck2",
      description: "This is a deck",
      blocked: true,
      cards: [
        { id: 1, question: "Deck2 question1", answer: "Deck2 answer1" },
        { id: 2, question: "Deck2 question2", answer: "Deck2 answer2" }
      ]
    },
    {
      id: 3, 
      title: "Deck3",
      blocked: false,
      description: "This is a deck",
      cards: [
        { id: 1, question: "Deck3 question1", answer: "Deck3 answer1" },
        { id: 2, question: "Deck3 question2", answer: "Deck3 answer2" }
      ]
    }
  ]

  async function fetchDecks(){
    selectedUser = $adminSelectedUserStore;
    const myHeaders = new Headers();
    myHeaders.append("Authorization", "Bearer " + $tokenStore);
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
                <button class="btn btn-primary" on:click={()=>{$adminSelectedDeckStore=deck; redirect("admin/showcards")}}>ShowCards</button>
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
                <button class="btn btn-primary" on:click={()=>{$adminSelectedDeckStore=deck; redirect("admin/showcards")}}>ShowCards</button>
              </div>
            </div>
        </div>
      {/if}
    {/each}
  </div>
</main>