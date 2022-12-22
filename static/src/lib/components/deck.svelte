<script lang="ts">
    import { createEventDispatcher } from 'svelte';
    const dispatch = createEventDispatcher();

    export let deck; 
    let { id, title, description, cards, numberOfCardsLearned, numberOfCardsToLearn, published} = deck;
    
    
    let hover = false
    function handleMouseOut() {
        hover = false
    }
    function handleMouseOver() {
        hover = true
    }
    
    function handleEditDeck() {
        dispatch('editDeck', "editDeck");
    }
    function handleDeleteDeck() {
        dispatch('deleteDeck', "deleteDeck");
    }
    function handleListCards() {
        dispatch('listCards', "listCards");
    }
    function handleLearnDeck(){
        dispatch('learnDeck', "learnDeck");
    }
    function handlePublishDeck(){
        dispatch('publishDeck', "publishDeck");
        published = !published;
    }
    // TODO add delete Deck functionality
    // TODO add edit Deck modal 
    // TODO add publish Deck functionality
</script>


<!-- svelte-ignore a11y-mouse-events-have-key-events -->
<div class="bg-slate-900 rounded-xl shadow-xl p-5 h-96 relative" on:mouseover={handleMouseOver} on:mouseout={handleMouseOut}>
        <div class="{hover ? "hidden" : "block"}" >
            <h1 class="underline flex justify-center text-xl">{title}</h1>
            <br class="my-4"/>
            <p>{description}</p>
            <br class="my-4"/>
            <div class="bottom-0 absolute mb-4">
                <div class="grid grid-rows-5 gap-2">
                    <div class="badge badge-warning">Number of Cards: {cards.length}</div>
                    <div class="badge badge-secondary">Learned: {numberOfCardsLearned}</div>
                    <div class="badge badge-primary">ToLearn: {numberOfCardsToLearn}</div>
                    {#if published}
                        <div class="badge badge-info">Published</div>
                    {:else}
                        <div class="badge badge-error">Not Published</div>
                    {/if}
                    <progress class="progress progress-success w-56 bg-gray-600 border-gray-600 border-1" value="{numberOfCardsLearned}" max="{cards.length}"></progress>

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