<script lang="ts">
    import { createEventDispatcher } from 'svelte';
    const dispatch = createEventDispatcher();

    function handleEditDeck() {
        dispatch('editDeck', "editDeck");
    }
    import { onMount } from 'svelte';
    import { redirect } from '../utils/redirect';
    export let title = "title";
    export let description = "description";
    export let id = 0;
    export let learned = 0; 
    export let NotLearned = 0;
    export let published = false;
    let hover = false
    function handleMouseOut() {
        hover = false
    }
    function handleMouseOver() {
        hover = true
    }
    // TODO add delete Deck functionality
    // TODO add edit Deck modal 
    // TODO add publish Deck functionality
</script>


<!-- svelte-ignore a11y-mouse-events-have-key-events -->
<div class="bg-slate-900 rounded-xl shadow-xl p-10 h-96 relative" on:mouseover={handleMouseOver} on:mouseout={handleMouseOut}>
        <div class="{hover ? "hidden" : "block"}" >
            <h1 class="underline flex justify-center text-xl">{title}</h1>
            <br class="my-4"/>
            <p>{description}</p>
            <br class="my-4"/>
            <div class="bottom-0 absolute mb-4">
                <div class="flex">
                    <span>Learned: </span>
                    <span class="ml-2 flex justify-center">{learned}</span>
                </div>
                <div class="flex">
                    <span>Not learned: </span>
                    <span class="ml-2 flex justify-center">{NotLearned}</span>
                </div>
                <div>
                    {#if published}
                    <span>published</span>
                    {:else}
                    <span>Not published</span>
                    {/if}
                </div>
            </div>
        </div>

        <div class="{hover ? "block" : "hidden"} grid grid-row gap-2">
                <button class="btn btn-primary" on:click={()=>redirect("learn")}>Learn Deck</button>
                <button class="btn btn-primary" on:click={()=>redirect("listcards")}>List Cards</button>
                <label for="editDeckModal" class="btn btn-primary" on:click={handleEditDeck}>Edit Deck</label>
                <button class="btn {published ? "btn-secondary" : "btn-primary"}" on:click={()=> published = !published}>Publish Deck</button>
                <button class="btn btn-primary">Delete Deck</button>
        </div>
</div>