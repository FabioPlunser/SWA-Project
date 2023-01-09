<script lang="ts">
  import { createEventDispatcher } from 'svelte';
  const dispatch = createEventDispatcher();
  export let card;

  function handleDeleteCard(card) {
      dispatch('deleteCard', card);
  }

  // 
  $: minHeight = `${1 +  card.backText.split("\n").length * 2.5}em`;
</script>


<div class="card p-5 w-auto bg-slate-900">
  {#if card.cardId}
      <h1 class="flex justify-center text-xl">Card {card.cardId.slice(0, 5)}</h1>
  {:else}
    <h1 class="flex justify-center text-xl">Card {card.id}</h1>
  {/if}

  <textarea bind:value={card.frontText} placeholder="question" class="textarea p-2 bg-slate-800 w-auto" />
  <br class="mt-4"/>
  <textarea bind:value={card.backText} placeholder="answer" class="textarea p-2 bg-slate-800 w-auto" style="min-height: {minHeight}" />

  <br class="mt-4"/>
  <div class="card-action">
      <button class="btn btn-accent" type="button" on:click={()=>handleDeleteCard(card)}>Delete Card</button>
  </div>
</div>