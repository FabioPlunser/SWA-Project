<script lang="ts">
    import favicon from "/favicon.png";
    import Nav from "../lib/components/nav.svelte";
    import SvelteToast from "../lib/components/SvelteToast.svelte";
	import DualSideCard from './../lib/components/dualSideCard.svelte';

    import { redirect } from '../lib/utils/redirect';
    import { tokenStore } from "../lib/stores/tokenStore";
    import { userSelectedDeckStore } from "../lib/stores/userSelectedDeckStore";
    import { handleLogout } from '../lib/utils/handleLogout';
  import { addToastByRes } from "../lib/utils/addToToastStore";
    
    $: if($tokenStore.length < 30) redirect("login");
    $: getCardsFromDecK();

    let navButtons = [
        { text: "DeckView", action: () => redirect("") },
        { text: "Logout", action: handleLogout }
    ];

    let newCards = [];
    let cards = [];
    let id = 0;

    const myHeaders = new Headers();
    myHeaders.append("Authorization", "Bearer " + $tokenStore);

    async function getCardsFromDecK(){
       
        let res = await fetch("/api/get-cards-from-deck?deckId=" + $userSelectedDeckStore.deckId, {
            method: "GET",
            headers: myHeaders,
        });
        res = await res.json();
        cards = res.items;
    }
    function addCard() {
        newCards.push({created: true, cardId: id, frontText: "", backText: ""});
        newCards = [...newCards];
        id++;
    }
    function handleDeleteNewCard(card) {
        newCards = newCards.filter(c => c.cardId !== card.cardId);
        newCards = [...newCards];
    }

    async function handleDeleteCard(card) {
        let res = await fetch("/api/delete-card?cardId=" + card.cardId, {
            method: "DELETE",
            headers: myHeaders,
        });
        res = await res.json();
        addToastByRes(res);
        getCardsFromDecK();
    }

    async function handleSubmit(e){
        const myHeaders = new Headers();
        myHeaders.append("Authorization", "Bearer " + $tokenStore);
        const formData = new FormData(e.target);
        
        cards.push(...newCards);

    
        formData.append("cards", JSON.stringify(cards));

        let res = await fetch("/api/update-deck-cards", {
            method: "POST",
            headers: myHeaders,
            body: formData,
        });
        res = await res.json();
        addToastByRes(res);
        getCardsFromDecK();
        newCards = [];
    }

    $: console.log(cards);
    
</script>


<svelte:head>
    <link rel="icon" type="image/png" href={favicon}/>
    <title>ListCards</title>
</svelte:head>

<Nav title="ListCards" buttons={navButtons}/>
<SvelteToast/>
<main class="mt-20 ">
    <div class="flex justify-center">
        <form class="mx-auto" method="POST" action="api/update-deck" on:submit|preventDefault={handleSubmit}>
            <h1 class="text-2xl">Cards of Deck <span class="text-red-600">{$userSelectedDeckStore.name}</span> </h1>

            <input name="deckId" type="hidden" bind:value={$userSelectedDeckStore.deckId}/>

            <div>
                <div class="tooltip" data-tip="Add Card">
                    <button class="btn btn-secondary" type="button" on:click={()=>{addCard()}}>Add Card</button>
                </div>
                <div class="tooltip" data-tip="Add Card">
                    <button class="ml-2 btn btn-primary" type="submit">Submit</button>
                </div>
            </div>
        </form>
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