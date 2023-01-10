<script lang="ts">
    import favicon  from '../assets/favicon.png';
    import Nav from "../lib/components/nav.svelte";
    import SvelteToast from "../lib/components/SvelteToast.svelte";
	import DualSideCard from './../lib/components/dualSideCard.svelte';

    import { redirect } from '../lib/utils/redirect';
    import { tokenStore } from "../lib/stores/tokenStore";
    import { userSelectedDeckStore } from "../lib/stores/userSelectedDeckStore";
    import { handleLogout } from '../lib/utils/handleLogout';
    import { fetching } from "../lib/utils/fetching";
    
    $: if($tokenStore.length < 30) redirect("login");
    $: getCardsFromDeck();

    let navButtons = [
        { text: "DeckView", action: () => redirect("") },
        { text: "Logout", action: handleLogout }
    ];

    let cards = [];

    async function getCardsFromDeck(){
        let res = await fetching("/api/get-cards-of-deck", "GET", [{name: "deckId", value: $userSelectedDeckStore.deckId}]);
        cards = res.items;
    }
    
</script>


<svelte:head>
    <link rel="icon" type="image/png" href={favicon}/>
    <title>ListCards</title>
    <script src="http://localhost:35729/livereload.js"></script>
</svelte:head>

<Nav title="ListCards" buttons={navButtons}/>
<SvelteToast/>
<main class="mt-20 m-10">
    {#if cards.length > 0}
    <div class="flex flex-row gap-2">
        {#each cards as card}
            <div>
                <DualSideCard {card}/>
            </div>
        {/each}
    </div>
    {:else}
        <h1 class="text-2xl text-center">No cards found</h1>
    {/if}
</main>