<script lang="ts">
    import { createEventDispatcher } from 'svelte';
    const dispatch = createEventDispatcher();

    import { addToastByRes } from '../utils/addToToastStore';
    import { fetching } from '../utils/fetching';

    export let deck; 
    let { deckId, name, description, published, blocked, cards} = deck;
    
    $: getAllCardsToLearn();
    $: getCardsOfDeck();

    let hover = false
    function handleMouseOut() {
        hover = false
    }
    function handleMouseOver() {
        hover = true
    }
    
    
    let cardsToLearn = [];

    async function getAllCardsToLearn(){
		let res = await fetching("/api/get-all-cards-to-learn", "GET", [{name: "deckId", value: deckId}]);
		cardsToLearn = res.items;	
	}

    async function getCardsOfDeck(){
        let res = await fetching("/api/get-cards-of-deck", "GET", [{name: "deckId", value: deckId}]);
        cards = res.items;
    }

    function handleListCards() {
        dispatch('listCards');
    }
    
    function handleLearnDeck(){
        dispatch('learnDeck');
    }


	async function handleUnsubscribe(){
		let res = await fetching(`/api/unsubscribe-deck`, "POST", [{name: "deckId", value: deckId}]); 
		addToastByRes(res);
        dispatch("unsubscribe")
	}	
</script>


<!-- svelte-ignore a11y-mouse-events-have-key-events -->
{#if !blocked && published}
<div class="bg-slate-900 rounded-xl shadow-xl p-5 h-96 relative" on:mouseover={handleMouseOver} on:mouseout={handleMouseOut}>
        <div class="{hover ? "hidden" : "block"}" >
            <h1 class="underline flex justify-center text-xl">{name}</h1>
            <br class="my-4"/>
            <p>{description}</p>
            <br class="my-4"/>
            <div class="bottom-0 absolute mb-4">
                <div class="grid grid-rows gap-2">
                    {#if cards}
                        <div class="badge badge-primary">Number of cards: {cards.length} </div>
                        {:else}
                        <div class="badge badge-error">No cards</div>
                    {/if}
                    {#if cardsToLearn}
                        <div class="badge badge-primary">Number of cards to learn: {cardsToLearn.length} </div>
                        {:else}
                        <div class="badge badge-error">No cards to learn</div>
                    {/if}
                    <!-- {#if cards && cardsToLearn}
                        Progress: <progress class="progress progress-success bg-gray-700" value={cards.length - cardsToLearn.length} max={cards.length}></progress>
                    {:else}
                        <div class="badge badge-error">No cards to learn</div>
                    {/if} -->
                </div>
            </div>
        </div>


        <div class="{hover ? "block" : "hidden"} grid grid-row gap-2">
            <button class="btn btn-primary" on:click={handleLearnDeck}>Learn Deck</button>
            <button class="btn btn-primary" on:click={handleListCards}>List Cards</button>
            <button class="btn btn-primary" on:click={handleUnsubscribe}>Unsubscribe</button>
        </div>       
</div>
{/if}

{#if blocked || !published}
<div class="bg-slate-900 rounded-xl shadow-xl p-5 h-auto relative opacity-50">
    <div>
        <h1 class="underline flex justify-center text-xl">{name}</h1>
        <br class="mt-4"/>
        <p class="flex justify-center">{description}</p>
    </div>
    <br class="mt-4"/>

    <button class="btn btn-primary" on:click={handleUnsubscribe}>Unsubscribe</button>
</div>
{/if}