<script lang="ts">
  import { createEventDispatcher } from 'svelte';
  const dispatch = createEventDispatcher();
  export let card;
  export let index = 0;
  export let editable = false;
  export let cardBg = "bg-slate-900";
  export let textBg = "bg-slate-800";

  function handleDeleteCard(card) {
      dispatch('deleteCard', card);
  }


  $: backTextMinHeight = `${1+card.frontText.split(" ").length}em`;
  $: frontTextMinHeight = `${1+card.backText.split(" ").length}em`
</script>

{#if editable}
  <div class="card p-5 w-auto {cardBg}">
    <h1 class="flex justify-center text-xl">Card {index}</h1>

    <textarea bind:value={card.frontText} placeholder="question" class="textarea p-2 bg-slate-800 w-auto" style="min-height: {frontTextMinHeight}"/>
    <br class="mt-4"/>
    <textarea bind:value={card.backText} placeholder="answer" class="textarea p-2 bg-slate-800 w-auto" style="min-height: {backTextMinHeight}" />

    <br class="mt-4"/>
    <div class="card-action">
        <button class="btn btn-accent" type="button" on:click={()=>handleDeleteCard(card)}>Delete Card</button>
    </div>
  </div>
{:else}
  <div class="card p-5 w-auto {cardBg}" >
    <h1 class="flex justify-center text-xl">Card {index}</h1>
  
    <textarea bind:value={card.frontText} readonly class="textarea p-2 w-auto {textBg}" style="min-height: {frontTextMinHeight}"/>
    <br class="mt-4"/>
    <textarea bind:value={card.backText} readonly class="textarea p-2 w-auto {textBg}" style="min-height: {backTextMinHeight}" />
  </div>
{/if}
