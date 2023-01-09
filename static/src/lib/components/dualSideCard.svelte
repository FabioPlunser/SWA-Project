<script lang="ts">
  import { createEventDispatcher } from 'svelte';
  const dispatch = createEventDispatcher();
  export let card;
  export let editable = false;
  export let cardStyle = "";
  export let textAreaStyle = "";

  function handleDeleteCard(card) {
      dispatch('deleteCard', card);
  }

  let frontText = "";
  let backText = "";

  if (card){
    frontText = card.frontText;
    backText = card.backText;
  }

  $: backTextMinHeight = `${1+backText.split(" ").length}em`;
  $: frontTextMinHeight = `${1+frontText.split(" ").length}em`
</script>

{#if editable}
  <div class="card p-5 w-auto bg-slate-900 {cardStyle}">
    {#if card.cardId}
        <h1 class="flex justify-center text-xl">Card {card.cardId.slice(0, 5)}</h1>
    {:else}
      <h1 class="flex justify-center text-xl">Card {card.id}</h1>
    {/if}

    <textarea bind:value={frontText} placeholder="question" class="textarea p-2 bg-slate-800 w-auto" style="min-height: {frontTextMinHeight}"/>
    <br class="mt-4"/>
    <textarea bind:value={backText} placeholder="answer" class="textarea p-2 bg-slate-800 w-auto" style="min-height: {backTextMinHeight}" />

    <br class="mt-4"/>
    <div class="card-action">
        <button class="btn btn-accent" type="button" on:click={()=>handleDeleteCard(card)}>Delete Card</button>
    </div>
  </div>
{:else}
  <div class="card p-5 w-auto bg-slate-900 {cardStyle}" >
    {#if card.cardId}
        <h1 class="flex justify-center text-xl">Card {card.cardId.slice(0, 5)}</h1>
    {:else}
      <h1 class="flex justify-center text-xl">Card {card.id}</h1>
    {/if}

    <textarea bind:value={frontText} readonly class="textarea p-2 bg-slate-800 w-auto {textAreaStyle}" style="min-height: {frontTextMinHeight}"/>
    <br class="mt-4"/>
    <textarea bind:value={backText} readonly class="textarea p-2 bg-slate-800 w-auto {textAreaStyle}" style="min-height: {backTextMinHeight}" />
  </div>
{/if}
