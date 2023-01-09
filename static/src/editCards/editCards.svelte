<script lang="ts">
    import favicon  from '../assets/favicon.png';
    import Nav from "../lib/components/nav.svelte";
    import SvelteToast from "../lib/components/SvelteToast.svelte";
	import DualSideCard from '../lib/components/dualSideCard.svelte';
    import Form from "../lib/components/Form.svelte";

    import { redirect } from '../lib/utils/redirect';
    import { tokenStore } from "../lib/stores/tokenStore";
    import { userSelectedDeckStore } from "../lib/stores/userSelectedDeckStore";
    import { handleLogout } from '../lib/utils/handleLogout';
    import { addToastByRes } from "../lib/utils/addToToastStore";
    import { fetching } from "../lib/utils/fetching";
    
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
        let res = await fetching("/api/get-cards-of-deck", "GET", [{name: "deckId", value: $userSelectedDeckStore.deckId}]);
        cards = res.items;
    }
    function addCard() {
        newCards.push({created: true, index: index, frontText: "", backText: ""});
        newCards = [...newCards];
        index++;
    }
    function handleDeleteNewCard(card) {
        newCards = newCards.filter(c => c.index !== card.index);
        newCards = [...newCards];
    }

    async function handleDeleteCard(card) {
        let res = await fetching("/api/delete-card", "DELETE", [{name: "deckId", value: card.cardId}]);
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
            <div class="flex justify-center flex-col">
                <h1 class="text-2xl">Cards of Deck <span class="text-red-600">{$userSelectedDeckStore.name}</span> </h1>
                <div class="flex justify-center flex-row ">
                    <div class="tooltip" data-tip="Add Card">
                        <button class="btn btn-secondary" type="button" on:click={()=>{addCard()}}>Add Card</button>
                    </div>
                    <div class="tooltip" data-tip="Add Card">
                        <button class="ml-2 btn btn-primary" type="submit">Submit Cards</button>
                    </div>
                </div>
            </div>
        </Form>
    </div>
    <br class="mt-4"/>
    <div class="grid grid-cols-3 gap-2">
        {#each newCards as card}
            <div>
                <DualSideCard {card} editable={true} on:deleteCard={()=>handleDeleteNewCard(card)} />
            </div>
        {/each}
        {#each cards as card}
            <div>
                <DualSideCard {card} editable={true} on:deleteCard={()=>handleDeleteCard(card)}/>
            </div>
        {/each}
        
    </div>
</main>