<script lang="ts">
  import favicon from "/favicon.png";
  import Nav from "../../lib/components/nav.svelte";
  import DualSideCard from "../../lib/components/dualSideCard.svelte";
  import Spinner from "../../lib/components/Spinner.svelte";
	import SvelteToast from './../../lib/components/SvelteToast.svelte';
  
  import { redirect } from '../../lib/utils/redirect';
  import { handleLogout } from "../../lib/utils/handleLogout";
	import { adminSelectedDeckStore } from '../../lib/stores/adminSelectedDeckStore';
  import { tokenStore} from '../../lib/stores/tokenStore';

  let buttons = [
    { text: "Back", action: () => redirect("admin/show-decks") },
    { text: "Admin", action: () => redirect("admin") },
    { text: "Home", action: () => redirect("") },
    { text: "Logout",action: () => handleLogout()}
  ];

  async function getCardsOfDeck(){ 
    let res = await fetch("/api/get-cards-of-deck", "GET", [{deckId: $adminSelectedDeckStore.deckId}]);
    if(res.success) return res.items;
    else addToastByRes(res);
  }
  
  let cards = getCardsOfDeck();
</script>

<svelte:head>
  <title>Admin ShowCards</title>
  <link rel="icon" type="image/png" href="{favicon}" />
</svelte:head>

<Nav title="ShowCards" {buttons}/>
<SvelteToast/>
<main class="mt-20 m-2">
  <h1 class="flex justify-center text-2xl underline">Cards of Deck {$adminSelectedDeckStore.title}</h1>
  <br/>
  {#await cards}
    <Spinner/>
  {:then cards}
    {#if cards.length == 0}
      <h1 class="flex justify-center text-2xl underline">No Cards</h1>
    {:else}
      <div class="grid grid-cols-4 gap-4">
        {#each cards as card}
          <DualSideCard {card}/>
        {/each}
      </div>
    {/if}
  {/await}
</main>