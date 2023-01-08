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

    let newCards = [];
    let cards = [];

    async function getCardsFromDeck(){
        let res = await fetching("/api/get-cards-of-deck", "GET", [{name: "deckId", value: $userSelectedDeckStore.deckId}]);
        cards = res.items;
    }
    
</script>


<svelte:head>
    <link rel="icon" type="image/png" href={favicon}/>
    <title>ListCards</title>
</svelte:head>

<Nav title="ListCards" buttons={navButtons}/>
<SvelteToast/>
<main class="mt-20 m-10">
    <div class="grid grid-cols-3 gap-2">
        {#each newCards as card}
            <div>
                <DualSideCard {card}/>
            </div>
        {/each}
        {#each cards as card}
            <div>
                <DualSideCard {card}/>
            </div>
        {/each}
        
    </div>
</main>