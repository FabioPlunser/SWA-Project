<script lang="ts">
  import favicon from '$assets/favicon.png';
  import Nav from '$components/nav.svelte';
	import SvelteToast from '$components/SvelteToast.svelte';
  import DualSideCard from '$components/dualSideCard.svelte';
  import Form from '$components/Form.svelte';
  import Markdown from '$components/markdown.svelte';
  import FormError from '$components/formError.svelte';
  import autosize from 'svelte-autosize';


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

  $: {
        if(cards.length > 0){
            window.onbeforeunload = confirmExit;
            function confirmExit() {
                return "You have attempted to leave this page. Are you sure?";
            }
        }else{
            window.onbeforeunload = null;
        }
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
<Nav title="CreateDeck" {buttons}/>
<main class="mt-20 m-8">
  <h1 class="flex justify-center text-3xl font-bold">Create Deck</h1>
  <br class="pt-4"/>
  <Form style="flex justify-center" url="/api/create-deck" method="POST" dataFormat={formFormat.JSON} {formValidators} addJSONData={[{cards: cards}]} on:postFetch={handlePostFetch}>
    <div class="max-w-full">
      <div class="bg-slate-900 p-5 rounded-xl">
        <div class="flex flex-col gap-1">
          <label class="input-group">
            <span class="w-40">Name</span>
            <input tabindex="1" name="name" type="text" bind:value={name} placeholder="Softwarearchitecture" class="input w-full bg-slate-800" />
          </label>
          <FormError name="name" key="required" message="Name is required"/>
          <FormError name="name" key="maxLength" message="Max length is 255"/>

          <br class="pt-4"/>
          
          <!-- svelte-ignore a11y-label-has-associated-control -->
          <label class="input-group">
            <span class="w-40">Description</span>
            <input type="hidden" name="description" bind:value={description}/>
            {#if descriptionFocus}
              <textarea tabindex="2" use:autosize on:mouseleave={()=>descriptionFocus=false} name="description" contenteditable id="divTextarea" bind:value={description} placeholder="Description" class="input bg-slate-800 min-h-[70px] h-auto w-full p-2 rounded-l-none  resize"/>
            {:else}
                <!-- svelte-ignore a11y-click-events-have-key-events -->
                <div tabindex="2" on:click={()=>descriptionFocus=true} class="input bg-slate-800 min-h-[70px] h-auto w-full p-2 rounded-l-none">
                    <div>
                        <Markdown data={description}/>
                    </div>
                </div>  
            {/if}
          </label>
          <FormError name="description" key="required" message="Description is required"/>
          
          <br class="pt-4"/>
          
          <label class="input-group">
          <span class="w-40">Visibility</span>
          <select tabindex="3" name="published" class="flex input w-full bg-slate-800">
              <option value={false}>private</option>
              <option value={true}>public</option>
          </select>
          </label>
      
          <br class="pt-4"/>

          <div class="flex flex-row justify-between">
            <button tabindex="4" class="btn btn-accent" type="button" on:click={()=>{addCard()}}>Add Card</button>
            <button tabindex="5" class="btn btn-primary" type="submit">Submit Deck</button>
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