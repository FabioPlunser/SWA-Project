<script lang="ts">
	import { createEventDispatcher } from 'svelte';
    const dispatch = createEventDispatcher();
    export let card;

    function handleDeleteCard(card) {
        dispatch('deleteCard', card);
    }
</script>


<div class="card p-5 w-fit bg-slate-900">
    {#if card.cardId}
        <h1 class="flex justify-center text-xl">Card {card.cardId.slice(0, 5)}</h1>
    {:else}
      <h1 class="flex justify-center text-xl">Card {card.index}</h1>
    {/if}
    <div class="flex flex-row p-2">
      <textarea  bind:value={card.frontText} placeholder="question" class="input p-2 bg-slate-800" />
      <div class="divider divider-horizontal"></div> 
      <textarea  bind:value={card.backText} placeholder="answer" class="input p-2 bg-slate-800" />
    </div>
    <div class="flex justify-center">
        <button class="btn btn-accent" type="button" on:click={()=>handleDeleteCard(card)}>Delete Card</button>
      </div>
</div>