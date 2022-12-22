<script lang="ts">
    import favicon from "/favicon.png";
    import Nav from "../lib/components/nav.svelte";
	import DualSideCard from './../lib/components/dualSideCard.svelte';
    import { redirect } from '../lib/utils/redirect';
    import { tokenStore } from "../lib/stores/tokenStore";
    import { userSelectedDeckStore } from "../lib/stores/userSelectedDeckStore";
    import { handleLogout } from '../lib/utils/handleLogout';
    $: if($tokenStore.length < 30) redirect("login");

    let navButtons = [
        { text: "DeckView", action: () => redirect("") },
        { text: "Logout", action: handleLogout }
    ];
    $: cards = $userSelectedDeckStore.cards;

    //TODO get cards from server and make them editable
    //TODO make cards draggable to change order
    //TODO make cards deletable
    //TODO make cards creatable
    //TODO send cards to server
</script>


<svelte:head>
    <link rel="icon" type="image/png" href={favicon}/>
    <title>ListCards</title>
</svelte:head>

<Nav title="ListCards" buttons={navButtons}/>


<main class="mt-20 ">

    <div class="flex justify-center mx-4">
        <div class="grid grid-cols-3 gap-y-3 gap-x-3">
            {#each cards as card}
                <DualSideCard {card}/>
            {/each}
        </div>
    </div>
</main>