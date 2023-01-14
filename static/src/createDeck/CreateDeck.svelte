<script lang="ts">
  import favicon from '$assets/favicon.png';
  import Nav from '$components/nav.svelte';
	import SvelteToast from '$components/SvelteToast.svelte';
  import DualSideCard from '$components/dualSideCard.svelte';
  import Form from '$components/Form.svelte';
  import SvelteMarkdown from 'svelte-markdown'

  import { fly } from "svelte/transition";
  import { addToastByRes } from '$utils/addToToastStore';
  import { Validators } from "$utils/Validators";
  
  let buttons = [
    { text: "Home", href: "/" },
  ]

  let cards = [];
  let id = 0;
  
  function addCard() {
    cards.push({id: id, frontText: "", backText: "", isFlipped: false});
    cards = [...cards];
    id++;
  }

  /**
   * When isFlipped is checked two cards need to be created one flipped and one not flipped
   */
  function updateCard(data){
    let card = data.detail;
    cards.map(c => {
      if(c.id === card.id){
        c = card;
      }
    });
    cards = [...cards];
  }

  function handleDeleteCard(card) {
    cards = cards.filter(c => c.id !== card.id);
    cards = [...cards];

  }

  let errors = {};
  let formValidators = {
    name: {
      validators: [Validators.required],
    },
    description: {
      validators: [Validators.required],
    },
    isPublished: {
      validators: [Validators.required],
    },
  };

  async function handlePostFetch(data){
    data.detail.e.target.reset();
    addToastByRes(data.detail.res);
    cards=[];
  }



  let MaxNumberChars = 255;
  let name = "";
  let description = "";
  let descriptionFocus = false;
</script>

<svelte:head>
  <title>Create Deck</title>
  <link rel="icon" type="image/png" href={favicon}>
  <script src="http://localhost:35729/livereload.js"></script>
</svelte:head>

<SvelteToast />
<Nav title="Decks" {buttons}/>
<main class="mt-20 m-8">
  <h1 class="flex justify-center text-3xl font-bold">Create Deck</h1>
  <br class="pt-4"/>
  <Form style="flex justify-center" url="/api/create-deck" method="POST" dataFormat="JSON" formValidators={formValidators} bind:errors addJSONData={[{cards: cards}]} on:postFetch={handlePostFetch}>
    <div class="max-w-full">
      <div class="bg-slate-900 p-5 rounded-xl">
        <div class="flex flex-col">
          <label class="input-group">
            <span class="w-40">Name</span>
            <input name="name" type="text" bind:value={name} placeholder="Softwarearchitecture" class="input w-full bg-slate-800" />
          </label>
          <div class="relative flex justify-between">
            {#if errors?.name?.required?.error}
              <span class="text-red-500">{errors.name.required.message}</span>
            {/if}
            <p class="absolute text-sm text-gray-400 right-0">{name.length}/{MaxNumberChars}</p>
          </div>
      
          <br class="pt-4"/>

          <input name="description" type="hidden" bind:value={description} />

          <!-- svelte-ignore a11y-label-has-associated-control -->
          <label class="input-group">
            <span class="w-40">Description</span>
            {#if descriptionFocus}
              <textarea on:blur={()=>descriptionFocus=false} contenteditable id="divTextarea" bind:value={description} placeholder="Description" class="bg-slate-800  min-h-full  w-full p-2 rounded-xl"/>
            {:else}
              <!-- svelte-ignore a11y-click-events-have-key-events -->
              <div on:click={()=>descriptionFocus=true} id="divTextarea" placeholder="Description" class="bg-slate-800 min-h-full w-full p-2 rounded-xl prose prose-sm"><SvelteMarkdown source={description}/></div>  
            {/if}
          </label>
          <div class="relative flex justify-between">
            {#if errors?.description?.required?.error}
              <span class="text-red-500">{errors.description.required.message}</span>
            {/if}
          </div>
          
          <br class="pt-4"/>
          
          <label class="input-group">
          <span class="w-40">Publish</span>
          <select name="published" class="flex input w-full bg-slate-800">
              <option value={false}>false</option>
              <option value={true}>true</option>
          </select>
          </label>
          {#if errors?.isPublished?.required?.error}
            <span class="text-red-500">{errors.isPublished.required.message}</span>
          {/if} 
      
          <br class="pt-4"/>

          <div class="flex justify-center">
            <button class="btn btn-primary" type="submit">Submit Deck</button>
            
          </div>
      </div>
      <br class="pt-4"/>
      <h1 class="flex justify-center text-3xl font-bold">Cards</h1>
      <br class="pt-4"/>
      <div class="flex justify-center">
        <div class="tooltip flex justify-center" data-tip="Add Card">
          <button class="flex justify-center btn btn-accent" type="button" on:click={()=>{addCard()}}>Add Card</button>
        </div>
      </div>
    </div>
  </Form>
  
  <br class="mt-4"/>
  <div class="grid grid-cols-4 gap-2">
    {#each cards as card, i (card.id)}
      <div in:fly={{y: -100, duration: 300}} out:fly={{y: 100, duration: 300}}>
        <DualSideCard {card} flippable={true} on:isFlipped={updateCard} index={i+1} editable={true} on:deleteCard={()=>handleDeleteCard(card)} />
      </div>
    {/each}
  </div>
</main>

<style>
  #divTextarea {
      -moz-appearance: textfield-multiline;
      -webkit-appearance: textarea;
      /* border: 1px solid gray; */
      /* font: medium -moz-fixed; */
      /* font: -webkit-small-control; */
  }
</style>