<script lang="ts">
    import { createEventDispatcher } from 'svelte';
    const dispatch = createEventDispatcher();

    import { tokenStore } from "../stores/tokenStore";
    import { addToast, addToastByRes } from '../utils/addToStore';



    export let deck; 

    console.log("deck", deck);
    let { deckId, name, description, published} = deck;
    
    
    let hover = false
    function handleMouseOut() {
        hover = false
    }
    function handleMouseOver() {
        hover = true
    }
    

    const myHeaders = new Headers();
    myHeaders.append("Authorization", "Bearer " + $tokenStore);
    
    function handleEditDeck() {
        dispatch('editDeck', "editDeck");
    }
    
    async function handlePublishDeck(){
        published = !published;
        let res = await fetch(`/api/set-publicity?deckId=${deckId}&isPublished=${published}`, {
            method: "PUT",
            headers: myHeaders,
        });
        res = await res.json();
        addToastByRes(res);
        dispatch('publishDeck', "publishDeck");    
    }

    async function handleDeleteDeck() {
        let res = await fetch(`/api/delete-deck?deckId=${deckId}`, {
            method: "DELETE",
            headers: myHeaders,
        });
        res = await res.json();
        addToastByRes(res);
        dispatch('deleteDeck', "deleteDeck");
    }
    
    function handleListCards() {
        dispatch('listCards', "listCards");
    
    }
    
    function handleLearnDeck(){
        dispatch('learnDeck', "learnDeck");
    }
    
    // TODO add delete Deck functionality
    // TODO add edit Deck modal 
    // TODO add publish Deck functionality
</script>


<!-- svelte-ignore a11y-mouse-events-have-key-events -->
<div class="bg-slate-900 rounded-xl shadow-xl p-5 h-96 relative" on:mouseover={handleMouseOver} on:mouseout={handleMouseOut}>
        <div class="{hover ? "hidden" : "block"}" >
            <h1 class="underline flex justify-center text-xl">{name}</h1>
            <br class="my-4"/>
            <p>{description}</p>
            <br class="my-4"/>
            <div class="bottom-0 absolute mb-4">
                <div class="grid grid-rows-5 gap-2">
                    {#if published}
                        <div class="badge badge-info">Published</div>
                    {:else}
                        <div class="badge badge-error">Not Published</div>
                    {/if}
                    <!-- <progress class="progress progress-success w-56 bg-gray-600 border-gray-600 border-1" value="{numberOfCardsLearned}" max="{cards.length}"></progress> -->
                </div>
            </div>
        </div>

        <div class="{hover ? "block" : "hidden"} grid grid-row gap-2">
                <button class="btn btn-primary" on:click={handleLearnDeck}>Learn Deck</button>
                <button class="btn btn-primary" on:click={handleListCards}>List Cards</button>
                <button class="btn btn-primary" on:click={handleEditDeck}>Edit Deck</button>
                <button class="btn {published ? "btn-secondary" : "btn-primary"}" on:click={handlePublishDeck}>Publish Deck</button>
                <button class="btn btn-primary" on:click={handleDeleteDeck}>Delete Deck</button>
        </div>
</div>