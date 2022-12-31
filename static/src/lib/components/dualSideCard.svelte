<script lang="ts">
	import { createEventDispatcher } from 'svelte';
    const dispatch = createEventDispatcher();
    export let card;

    function handleDeleteCard(card) {
        dispatch('deleteCard', card);
    }
</script>


<div class="card bg-slate-800 p-5 w-fit">
    <h1 class="flex justify-center text-xl">Card {card.cardId}</h1>
    <div class="flex flex-row p-2">
      <textarea name="question" required bind:value={card.frontText} placeholder="question" class="input input-bordered w-fit bg-slate-900" />
      <div class="divider divider-horizontal"></div> 
      <textarea name="answer" required bind:value={card.backText} placeholder="answer" class="input input-bordered w-fit bg-slate-900" />
    </div>
    <div class="flex justify-center">
        <button class="btn btn-accent" type="button" on:click={()=>handleDeleteCard(card)}>Delete Card</button>
      </div>
</div>