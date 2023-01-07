<script lang="ts">
    import favicon  from '../assets/favicon.png';
    import Nav from "../lib/components/nav.svelte";
    import SvelteToast from "../lib/components/SvelteToast.svelte";
	import DualSideCard from './../lib/components/dualSideCard.svelte';

    import { redirect } from '../lib/utils/redirect';
    import { tokenStore } from "../lib/stores/tokenStore";
    import { userSelectedDeckStore } from "../lib/stores/userSelectedDeckStore";
    import { handleLogout } from '../lib/utils/handleLogout';
    import { addToastByRes } from "../lib/utils/addToToastStore";
    import { fetching } from "../lib/utils/fetching";
  import Form from "../lib/components/form.svelte";
    
    $: if($tokenStore.length < 30) redirect("login");
    $: getCardsFromDeck();

    let navButtons = [
        { text: "DeckView", action: () => redirect("") },
        { text: "Logout", action: handleLogout }
    ];

    let newCards = [];
    let cards = [];
    let index = 0;

    async function getCardsFromDeck(){
        let res = await fetching("/api/get-cards-of-deck", "GET", [{deckId: $userSelectedDeckStore.deckId}]);
        cards = res.items;
    }
    function addCard() {
        newCards.push({created: true, index: index, frontText: "SOLID", backText: "Single Responsibility"});
        newCards = [...newCards];
        index++;
    }
    function handleDeleteNewCard(card) {
        newCards = newCards.filter(c => c.index !== card.index);
        newCards = [...newCards];
    }

    async function handleDeleteCard(card) {
        let res = await fetching("/api/delete-card", "DELETE", [{cardId: card.cardId}]);
        addToastByRes(res);
        getCardsFromDeck();
    }
    
    function handlePostFetch(data){
        let res = data.detail.res;
        addToastByRes(res);
        getCardsFromDeck();
        newCards = [];
    }

    $: data = {
        deckId: $userSelectedDeckStore.deckId,
        name: $userSelectedDeckStore.name,
        description: $userSelectedDeckStore.description,
        isPublished: $userSelectedDeckStore.isPublished,
        cards: cards
    }
</script>


<svelte:head>
    <link rel="icon" type="image/png" href={favicon}/>
    <title>ListCards</title>
</svelte:head>

<Nav title="ListCards" buttons={navButtons}/>
<SvelteToast/>
<main class="mt-20 ">
    <div class="flex justify-center">
        <Form url="/api/update-deck" method="POST" on:preFetch={()=>cards.push(...newCards)} addJSONData={[data]} on:postFetch={handlePostFetch}>
            <h1 class="text-2xl">Cards of Deck <span class="text-red-600">{$userSelectedDeckStore.name}</span> </h1>
            <!-- <input name="deckId" type="hidden" bind:value={$userSelectedDeckStore.deckId}/> -->
            <div>
                <div class="tooltip" data-tip="Add Card">
                    <button class="btn btn-secondary" type="button" on:click={()=>{addCard()}}>Add Card</button>
                </div>
                <div class="tooltip" data-tip="Add Card">
                    <button class="ml-2 btn btn-primary" type="submit">Update Cards</button>
                </div>
            </div>
        </Form>
    </div>
    <br class="mt-4"/>
    <div class="grid grid-cols-4 gap-2">
        {#each newCards as card}
            <DualSideCard {card} on:deleteCard={()=>handleDeleteNewCard(card)}/>
        {/each}
        {#each cards as card}
            <DualSideCard {card} on:deleteCard={()=>handleDeleteCard(card)}/>
        {/each}
        
    </div>
</main>