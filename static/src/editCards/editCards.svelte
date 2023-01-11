<script lang="ts">
    import favicon  from '../assets/favicon.png';
    import Nav from "../lib/components/nav.svelte";
    import SvelteToast from "../lib/components/SvelteToast.svelte";
	import DualSideCard from '../lib/components/dualSideCard.svelte';
    import Form from "../lib/components/Form.svelte";

    import { fly } from "svelte/transition";
    import { redirect } from '../lib/utils/redirect';
    import { userSelectedDeckStore } from "../lib/stores/userSelectedDeckStore";
    import { handleLogout } from '../lib/utils/handleLogout';
    import { addToastByRes } from "../lib/utils/addToToastStore";
    import { fetching } from "../lib/utils/fetching";
    
    $: getCardsFromDeck();
    $: console.log("cards", cards);
    let navButtons = [
        { text: "DeckView", action: () => redirect("") },
        { text: "Logout", action: handleLogout }
    ];

    let showNewCards = true;
    let showExistingCards = true;

    let newCards = [];
    let index = 1;
    let newCard = {
        id: index,
        frontText: "",
        backText: "",
        newCard: true
    }
    
    let cards = [];
    async function getCardsFromDeck(){
        let res = await fetching("/api/get-cards-of-deck", "GET", [{name: "deckId", value: $userSelectedDeckStore.deckId}]);
        cards = res.items;
    }
    function addCard() {
        // cards.push(newCard);
        newCards.push(newCard);
        index++;
        newCard={
            id: index,
            frontText: "",
            backText: "",
            newCard: true
        }
        newCards = [...newCards];
    }
    

    async function handleDeleteCard(card) {
        console.log("card", card);
        if(card.newCard){
            console.log("card", card);
            newCards = newCards.filter(c => c.id !== card.id);
            newCards = [...newCards];
        }else{
            let res = await fetching("/api/delete-card", "DELETE", [{name: "cardId", value: card.cardId}]);
            addToastByRes(res);
            getCardsFromDeck();
        }

       
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
    <script src="http://localhost:35729/livereload.js"></script>
</svelte:head>

<Nav title="ListCards" buttons={navButtons}/>
<SvelteToast/>
<main class="mt-20 m-16">
    <div class="flex justify-center">
        <Form url="/api/update-deck" method="POST" on:preFetch={()=>cards.push(...newCards)} addJSONData={[data]} on:postFetch={handlePostFetch}>
            <div class="flex justify-center flex-col">
                <h1 class="text-3xl font-bold">Cards of Deck {$userSelectedDeckStore.name}</h1>
                <div class="flex justify-center flex-row ">
                    <div class="tooltip" data-tip="Update Cards of deck">
                        <button class="ml-2 btn btn-primary" type="submit">Update Cards</button>
                    </div>
                </div>
            </div>
        </Form>
    </div>
    <br class="mt-4"/>

    <div class="flex justify-center">
        <div class="card p-5 w-auto bg-slate-900">
            <h1 class="flex justify-center text-3xl font-bold">New Card</h1>
            <br class="mt-4"/>
            
            <textarea bind:value={newCard.frontText} placeholder="question" class="textarea p-2 bg-slate-800 w-full"/>
            <br class="mt-4"/>
            <textarea bind:value={newCard.backText} placeholder="answer" class="textarea p-2 bg-slate-800 w-full" />
        
            <br class="mt-4"/>
            
            <div class="card-action flex justify-center">
                <button class="btn btn-primary mx-1" on:click={()=>addCard()}>Add Card</button>
            </div>
        </div>
    </div>

    <br class="mt-4"/>
    <div class="">
        <h1 class="flex justify-center text-3xl font-bold">Cards</h1>
        <br class="pt-4"/>
        <div class="flex justify-center gap-4">
            <div class="flex items-center">
                <span class="mx-2">Show existing cards: </span><input type="checkbox" class="flex items-center toggle toggle-info" bind:checked={showExistingCards} />
                
            </div>
            <div class="flex items-center">
                <span class="mx-2">Show new cars: </span> <input type="checkbox" class="flex items-center toggle toggle-info" bind:checked={showNewCards} />
            </div>
        </div>  

        <br class="pt-4"/>
        <div class="grid grid-cols-5 gap-2">
            {#if showNewCards}
                {#each newCards as card, i (card.id)}
                    <div in:fly={{y: -100, duration: 300}} out:fly={{y: 100, duration: 100}}>
                        <DualSideCard title="New Card" cardBg="bg-slate-700"  {card} index={i+1} editable={true} on:deleteCard={()=>handleDeleteCard(card)}/>
                    </div>
                {/each}
            {/if}
            {#if showExistingCards}
                {#each cards as card, i (card.cardId)}
                    <div in:fly={{y: -100, duration: 300}} out:fly={{y: 100, duration: 100}}>
                        <DualSideCard title="Existing Card" {card} index={i+1} editable={true} on:deleteCard={()=>handleDeleteCard(card)}/>
                    </div>
                {/each}
            {/if}
        </div>
    </div>  
</main>