<script lang="ts">
  import favicon from '/favicon.png';
  import Nav from '../lib/components/nav.svelte';
	import SvelteToast from '../lib/components/SvelteToast.svelte';
  import DualSideCard from './../lib/components/dualSideCard.svelte';
  import Form from '../lib/components/form.svelte';

  import { redirect } from "../lib/utils/redirect";
  import { handleLogout } from '../lib/utils/handleLogout';
  import { addToastByRes } from '../lib/utils/addToToastStore';
  import { tokenStore } from "../lib/stores/tokenStore";
  import { Validators, validateForm, isFormValid} from "../lib/utils/Validators";


  let buttons = [
    { text: "Home", action: () => redirect("") },
    { text: "Logout", action: () => handleLogout()}
  ]

  let cards = [];
  let id = 0;

  function addCard() {
    cards.push({id: id, frontText: "SOLID", backText: "Single Responsibility"});
    cards = [...cards];
    id++;
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

</script>

<svelte:head>
  <title>Create Deck</title>
  <link rel="icon" type="image/png" href={favicon}>
</svelte:head>

<SvelteToast />
<Nav title="Decks" {buttons}/>
<main class="mt-20 m-2">
  <h1 class="flex justify-center text-2xl underline">Create Deck</h1>
  <br class="pt-4"/>
  <Form style="flex justify-center" url="/api/create-deck" method="POST" dataFormat="JSON" formValidators={formValidators} bind:errors  addJSONData={[{cards: cards}]} on:postFetch={handlePostFetch}>
    <div class="flex flex-col">
      <div class="form-control">
        <label class="input-group">
        <span class="w-36">Name</span>
        <input name="name" type="text" placeholder="Softwarearchitecture" class="input input-bordered w-full bg-slate-900" />
        </label>
        {#if errors?.name?.required?.error}
          <span class="text-red-500">{errors.name.required.message}</span>
        {/if}
      </div>
      <br class="pt-4"/>
      <div class="form-control">
        <label class="input-group">
        <span class="w-36">Description</span>
        <textarea name="description" placeholder="A deck to learn softwarearchitecture" class="textarea input-bordered w-full bg-slate-900" />
        </label>
        {#if errors?.description?.required?.error}
          <span class="text-red-500">{errors.description.required.message}</span>
        {/if}
      </div>
      <br class="pt-4"/>
      <div class="form-control">
        <label class="input-group">
        <span class="w-36">Publish</span>
        <select name="published" class="flex input w-full bg-slate-900">
            <option value={false}>false</option>
            <option value={true}>true</option>
        </select>
        </label>
        {#if errors?.isPublished?.required?.error}
          <span class="text-red-500">{errors.isPublished.required.message}</span>
        {/if}
      </div>

      <br class="pt-4"/>

      <div class="flex justify-center">
        <button class="btn btn-primary" type="submit">Submit</button>
      </div>

      <br class="pt-4"/>
      <h1 class="flex justify-center text-2xl underline">Cards</h1>
      <br class="pt-4"/>
      <div class="tooltip" data-tip="Add Card">
        <button class="btn btn-accent" type="button" on:click={()=>{addCard()}}>Add Card</button>
      </div>
    </div>
  </Form>
  
  <br class="mt-4"/>
  <div class="grid grid-cols-4 gap-2">
    {#each cards as card}
      <DualSideCard {card} on:deleteCard={()=>handleDeleteCard(card)}/>
    {/each}
  </div>
</main>