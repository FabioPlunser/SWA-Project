<script lang="ts">
	import autosize from 'svelte-autosize';
  import { createEventDispatcher } from 'svelte';
  const dispatch = createEventDispatcher();
  import SvelteMarkdown from 'svelte-markdown';

  export let card;
  export let index = 0;
  export let editable = false;
  export let flippable = false;
  let isFlipped = false;
  export let cardBg = "bg-slate-900";
  export let textBg = "bg-slate-800";
  export let title = "";

  let cardQuestionFocus = false; 
  let cardAnswerFocus = false;
  let answer = "";
  let question = "";


  function handleDeleteCard(card) {
      dispatch('deleteCard', card);
  }


  function handleFlip(){
    card.isFlipped = isFlipped;
    dispatch('isFlipped', card);
  }

  $: backTextMinHeight = `${1+card.frontText.split(" ").length}em`;
  $: frontTextMinHeight = `${1+card.backText.split(" ").length}em`
</script>

{#if editable}
  <div class="card p-5 w-auto {cardBg}">
    {#if title}
      <h1 class="flex justify-center text-xl">{title} {index}</h1>
    {:else}
      <h1 class="flex justify-center text-xl">Card {index}</h1>
    {/if}

    {#if cardQuestionFocus}
      <textarea use:autosize on:blur={()=>cardQuestionFocus=false} contenteditable id="divTextarea" bind:value={card.frontText} placeholder="Question" class="bg-slate-800  min-h-full  w-full p-2 rounded-xl"/>
    {:else}
      <!-- svelte-ignore a11y-click-events-have-key-events -->
      <div on:click={()=>cardQuestionFocus=true} id="divTextarea" placeholder="description" class="bg-slate-800 min-h-[60px] w-full p-2 rounded-xl prose prose-sm prose-dark">
        <SvelteMarkdown bind:source={card.frontText}/>
      </div>  
    {/if}

    <br class="mt-4"/>

    {#if cardAnswerFocus}
      <textarea use:autosize on:blur={()=>cardAnswerFocus=false} contenteditable id="divTextarea" bind:value={card.backText} placeholder="Question" class="bg-slate-800  min-h-full  w-full p-2 rounded-xl"/>
    {:else}
      <!-- svelte-ignore a11y-click-events-have-key-events -->
      <div on:click={()=>cardAnswerFocus=true} id="divTextarea" placeholder="description" class="bg-slate-800 min-h-[60px] w-full p-2 rounded-xl prose prose-sm prose-dark">
        <SvelteMarkdown bind:source={card.backText}/>
      </div>  
    {/if}

    <input type="hidden" bind:value={card.frontText}/>
    <input type="hidden" bind:value={card.backText}/>
    <!-- <textarea bind:value={card.frontText} type="hidden" placeholder="question" class="textarea p-2 bg-slate-800 w-full" style="min-height: {frontTextMinHeight}"/> -->
    <br class="mt-4"/>
    <!-- <textarea bind:value={card.backText} placeholder="answer" class="textarea p-2 bg-slate-800 w-full" style="min-height: {backTextMinHeight}" /> -->

    <br class="mt-4"/>
    <div class="card-action flex justify-center">
        <button class="btn btn-accent" type="button" on:click={()=>handleDeleteCard(card)}>Delete Card</button>
        {#if flippable}
          <div>
            <span class="ml-4">Learn both sides:</span>
            <div class="flex justify-center">
              <input type="checkbox" bind:checked={isFlipped} on:change={handleFlip} class="ml-4 flex mx-auto justify-center items-center checkbox checkbox-primary"/>
            </div>
          </div>
        {/if}
    </div>
  </div>
{:else}
  <div class="card p-5 w-auto {cardBg}" >
    <h1 class="flex justify-center text-xl">Card {index}</h1>

    
    <div id="divTextarea" class="textarea {textBg} p-2 w-auto resize-none  prose prose-sm prose-dark">
      <SvelteMarkdown bind:source={card.frontText}/>
    </div>  
    <br class="mt-4"/>
    <div id="divTextarea" class="textarea {textBg} p-2 w-auto resize-none  prose prose-sm prose-dark">
      <SvelteMarkdown bind:source={card.backText}/>
    </div> 

    <!-- <textarea bind:value={card.frontText} readonly class="textarea p-2 w-auto {textBg} resize-none" style="min-height: {frontTextMinHeight}"/>
    <br class="mt-4"/>
    <textarea bind:value={card.backText} readonly class="textarea p-2 w-auto {textBg} resize-none" style="min-height: {backTextMinHeight}" /> -->
    {#if card?.isFlipped}
      <div class="badge badge-primary">
        <span>Card is doubled to learn</span>
      </div>
    {/if}
  </div>
{/if}


<style>
  #divTextarea {
      -moz-appearance: textfield-multiline;
      -webkit-appearance: textarea;
  }
</style>