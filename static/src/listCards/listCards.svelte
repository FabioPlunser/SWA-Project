<script lang="ts">
    import favicon  from '$assets/favicon.png';
    import Nav from "$components/nav.svelte";
    import SvelteToast from "$components/SvelteToast.svelte";
	import DualSideCard from '$components/dualSideCard.svelte';

    import { redirect } from '$utils/redirect';
    import { userSelectedDeckStore } from "$stores/userSelectedDeckStore";
    import { handleLogout } from '$utils/handleLogout';
    import { fetching } from "$utils/fetching";
    
    $: getCardsFromDeck();

    let navButtons = [
        { text: "DeckView", href: "/"},
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