<script lang="ts">
  import favicon  from '$assets/favicon.png';
  import Nav from "$components/nav.svelte";
  import DualSideCard from "$components/dualSideCard.svelte";
  import Spinner from "$components/Spinner.svelte";
	import SvelteToast from '$components/SvelteToast.svelte';
  
	import { adminSelectedDeckStore } from '$stores/adminSelectedDeckStore';
  import { addToastByRes } from "$utils/addToToastStore";
  import { fetching } from "$utils/fetching";

  let buttons = [
    { text: "Back", href: "/admin/show-decks" },
    { text: "Home", href: "/" },
    { text: "Admin", href: "/admin" },
  ];

  async function getCardsOfDeck(){ 
    let res = await fetching("/api/get-cards-of-deck", "GET", [{name:"deckId", value: $adminSelectedDeckStore.deckId}]);
    if(res.success) return res.items;
    else addToastByRes(res);
  }
  
  let cards = getCardsOfDeck();
</script>

<svelte:head>
  <title>Admin ShowCards</title>
  <link rel="icon" type="image/png" href="{favicon}" />
  <script src="http://localhost:35729/livereload.js"></script>
</svelte:head>

<Nav title="ShowCards" {buttons}/>
<SvelteToast/>
<main class="mt-20 m-2">
  <h1 class="flex justify-center text-2xl font-bold">Cards of Deck {$adminSelectedDeckStore.title}</h1>
  <br/>
  {#await cards}
    <Spinner/>
  {:then cards}
    {#if cards.length == 0}
      <h1 class="flex justify-center text-2xl font-bold">No Cards</h1>
    {:else}
      <div class="grid grid-cols-4 gap-4">
        {#each cards as card, i}
          <DualSideCard {card} index={i+1}/>
        {/each}
      </div>
    {/if}
  {/await}
</main>