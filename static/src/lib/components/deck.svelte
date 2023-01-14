<script lang="ts">
    import { createEventDispatcher } from 'svelte';
    const dispatch = createEventDispatcher();

    import { addToastByRes } from '$utils/addToToastStore';
    import { fetching } from '$utils/fetching';
    import SvelteMarkdown from 'svelte-markdown'
    import { marked } from 'marked';

    export let deck; 
    let { deckId, name, description, published, blocked, numCards, numCardsToRepeat, numNotLearnedCards} = deck;
    

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

    async function handlePublishDeck(){
        published = !published;
        if(published){
            let res = await fetching("/api/publish-deck", "POST", [{name: "deckId", value: deckId}]);
            addToastByRes(res);
        }
        if(!published){
            let res = await fetching("/api/unpublish-deck", "POST", [{name: "deckId", value: deckId}]);
            addToastByRes(res);
        }
    }

    async function handleDeleteDeck() {
        let res = await fetching("/api/delete-deck", "DELETE", [{name: "deckId", value: deckId}]);
        addToastByRes(res);
        dispatch('deleteDeck');
    }
    
    function handleListCards() {
        dispatch('listCards');
    }
    
    function handleLearnDeck(){
        dispatch('learnDeck');
    }
</script>


<!-- svelte-ignore a11y-mouse-events-have-key-events -->
{#if !blocked}
<div class="bg-slate-900 rounded-xl shadow-xl p-5 h-96 relative" on:mouseover={handleMouseOver} on:mouseout={handleMouseOut}>
        <div class="{hover ? "hidden" : "block"} max-h-full" >
            <h1 class="font-bold flex justify-center text-3xl">{name}</h1>
            <br class="pt-4"/>
            <!-- <div id="divTextarea" class="prose prose-dark prose-sm prose-invert overflow-auto max-h-fit h-fit">
                {@html marked(description)}
            </div> -->
            <div class="max-h-full overflow-hidden break-all">
                <div id="divTextarea" class="break-all overflow-hidden min-h-[60px] w-full p-2 rounded-xl prose prose-sm prose-dark">
                    {@html marked(description)}
                    <!-- <SvelteMarkdown bind:source={description}/> -->
                </div>
            </div>
            <!-- <textarea class="resize-none bg-transparent" readonly>{description}</textarea> -->
            <br class="pt-8"/>

            <div class="bottom-0 absolute mb-4 mt-4">
                <div class="grid grid-rows gap-2">
                    <div class="gird grid-cols gap-2">
                        {#if numCards > 0}
                            <div class="badge badge-primary">Cards: {numCards} </div>
                            {:else}
                            <div class="badge badge-error">No cards</div>
                        {/if}
                        {#if numCardsToRepeat >0}
                            <div class="badge badge-primary">To repeat: {numCardsToRepeat} </div>
                        {:else}
                            <div class="badge badge-error">Nothing to repeat</div>
                        {/if}
                    </div>
                    <div class="gird grid-cols gap-2">
                        {#if numNotLearnedCards > 0}
                            <div class="badge badge-primary">To learn: {numNotLearnedCards} </div>
                        {:else}
                            <div class="badge badge-error">No cards to learn</div>
                        {/if}
                        {#if published}
                        <div class="badge badge-info">Published</div>
                        {:else}
                        <div class="badge badge-error">Not Published</div>
                        {/if}
                    </div>
                    <!-- Progress: <progress class="progress progress-success bg-gray-700" value={numCards - numCardsToLearn} max={numCards}></progress> -->
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
{/if}

{#if blocked}
<div class="bg-slate-900 rounded-xl shadow-xl p-5 relative opacity-50">
    <div >
        <h1 class="flex justify-center text-xl">{name}</h1>
        <br class="mt-4"/>
        <p>{description}</p>
        <br class="mt-4"/>
        <button class="btn btn-primary" on:click={handleDeleteDeck}>Delete Deck</button>
    </div>
</div>
{/if}


<style>
    #divTextarea {
        -moz-appearance: textfield-multiline;
        -webkit-appearance: textarea;
        /* border: 1px solid gray; */
        /* font: medium -moz-fixed; */
        /* font: -webkit-small-control; */
    }
  </style>