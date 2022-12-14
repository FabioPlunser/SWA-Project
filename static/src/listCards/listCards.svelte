<script lang="ts">
    import favicon from "/favicon.png";
    import Nav from "../lib/components/nav.svelte";
    import { redirect } from '../lib/utils/redirect';
    import { token } from "../lib/stores/token";
    import { handleLogout } from '../lib/utils/handleLogout';
    import FlipCard from "../lib/components/flipCard.svelte";
    $: if($token.length < 30) redirect("login");

    let navButtons = [
        { tag: "button", id: "", text: "DeckView", action: () => redirect("") },
        { tag: "button", id: "", text: "Logout", action: handleLogout }
    ];

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
            {#each [...Array(16)] as _, i}
                <FlipCard editable={true}/>
            {/each}
        </div>
    </div>
</main>