<script lang="ts">
  import favicon from '$assets/favicon.png';
  import Nav from '$components/nav.svelte';
	import SvelteToast from '$components/SvelteToast.svelte';
  import DualSideCard from '$components/dualSideCard.svelte';
  import Form from '$components/Form.svelte';
  import SvelteMarkdown from 'svelte-markdown'
  import FormError from '$components/formError.svelte';

  import { fly } from "svelte/transition";
  import { addToastByRes } from '$utils/addToToastStore';
  import { Validators } from "$utils/Validators";
  import { formFormat } from '$lib/types/formFormat';
  
  let buttons = [
    { text: "Home", href: "/" },
  ]

  let cards = [];
  let id = 0;
  let formValidators = {
    name: {
      validators: [Validators.required, Validators.maxLength(255)],
    },
    description: {
      validators: [Validators.required],
    }
  };
  
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


  async function handlePostFetch(data){
    data.detail.e.target.reset();
    addToastByRes(data.detail.res);
    cards=[];
  }


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
  <Form style="flex justify-center" url="/api/create-deck" method="POST" dataFormat={formFormat.JSON} {formValidators} addJSONData={[{cards: cards}]} on:postFetch={handlePostFetch}>
    <div class="max-w-full">
      <div class="bg-slate-900 p-5 rounded-xl">
        <div class="flex flex-col">
          <label class="input-group">
            <span class="w-40">Name</span>
            <input name="name" type="text" bind:value={name} placeholder="Softwarearchitecture" class="input w-full bg-slate-800" />
          </label>
          <FormError name="name" key="required" message="Name is required"/>
          <FormError name="name" key="maxLength" message="Max length is 255"/>

      
          <br class="pt-4"/>
          
          <!-- svelte-ignore a11y-label-has-associated-control -->
          <label class="input-group">
            <span class="w-40">Description</span>
            <input type="hidden" name="description" bind:value={description}/>
            {#if descriptionFocus}
              <textarea on:blur={()=>descriptionFocus=false} contenteditable id="divTextarea" bind:value={description} placeholder="Description" class="bg-slate-800  min-h-full  w-full p-2 rounded-xl"/>
            {:else}
              <!-- svelte-ignore a11y-click-events-have-key-events -->
              <div on:click={()=>descriptionFocus=true} id="divTextarea" placeholder="Description" class="bg-slate-800 min-h-[60px] w-full p-2 rounded-xl prose prose-sm prose-dark"><SvelteMarkdown bind:source={description}/></div>  
            {/if}
          </label>
          <FormError name="description" key="required" message="Description is required"/>
          
          <br class="pt-4"/>
          
          <label class="input-group">
          <span class="w-40">Publish</span>
          <select name="published" class="flex input w-full bg-slate-800">
              <option value={false}>false</option>
              <option value={true}>true</option>
          </select>
          </label>
      
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
  }
</style>