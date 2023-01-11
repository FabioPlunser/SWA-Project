<script lang="ts">
  import { createEventDispatcher } from 'svelte';
  const dispatch = createEventDispatcher();
  export let card;
  export let index = 0;
  export let editable = false;
  export let flippable = false;
  let isFlipped = false;
  export let cardBg = "bg-slate-900";
  export let textBg = "bg-slate-800";
  export let title = "";

  function handleDeleteCard(card) {
      dispatch('deleteCard', card);
  }


  function handleFlip(){
    card.isFlipped = isFlipped;
    dispatch('isFlipped', card);
  }

  $: backTextMinHeight = `${1+card.frontText.split(" ").length}em`;
  $: frontTextMinHeight = `${1+card.backText.split(" ").length}em`

  let MaxNumberChars = 255;
</script>

{#if editable}
  <div class="card p-5 w-auto {cardBg}">
    {#if title}
      <h1 class="flex justify-center text-xl">{title} {index}</h1>
    {:else}
      <h1 class="flex justify-center text-xl">Card {index}</h1>
    {/if}

    <textarea bind:value={card.frontText} placeholder="question" class="textarea p-2 bg-slate-800 w-full" style="min-height: {frontTextMinHeight}"/>
    <br class="mt-4"/>
    <textarea bind:value={card.backText} placeholder="answer" class="textarea p-2 bg-slate-800 w-full" style="min-height: {backTextMinHeight}" />

    <br class="mt-4"/>
    <div class="card-action flex justify-center">
        <button class="btn btn-accent" type="button" on:click={()=>handleDeleteCard(card)}>Delete Card</button>
        {#if flippable}
          <label>
            <span class="ml-4">Learn both sites</span>
            <input type="checkbox" bind:checked={isFlipped} on:change={handleFlip} class="ml-4"/>
          </label>
        {/if}
    </div>
  </div>
{:else}
  <div class="card p-5 w-auto {cardBg}" >
    <h1 class="flex justify-center text-xl">Card {index}</h1>
  
    <textarea bind:value={card.frontText} readonly class="textarea p-2 w-auto {textBg} resize-none" style="min-height: {frontTextMinHeight}"/>
    <br class="mt-4"/>
    <textarea bind:value={card.backText} readonly class="textarea p-2 w-auto {textBg} resize-none" style="min-height: {backTextMinHeight}" />
    {#if card?.isFlipped}
      <div class="badge badge-primary">
        <span>Card is doubled to learn</span>
      </div>
    {/if}
  </div>
{/if}
