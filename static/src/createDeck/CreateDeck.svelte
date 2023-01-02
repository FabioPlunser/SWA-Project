<script lang="ts">
  import favicon from '/favicon.png';
  import Nav from '../lib/components/nav.svelte';
	import SvelteToast from '../lib/components/SvelteToast.svelte';
  import DualSideCard from './../lib/components/dualSideCard.svelte';

  
  import { redirect } from "../lib/utils/redirect";
  import { handleLogout } from '../lib/utils/handleLogout';
  import { addToast, addToastByRes } from '../lib/utils/addToStore';
  import { tokenStore } from "../lib/stores/tokenStore";
	import { personIdStore } from "../lib/stores/personIdStore";


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

  async function handleSubmit(event) {
    const action = event.target.action;
    const method = event.target.method;
    const myHeader = new Headers()
    myHeader.append("Content-Type", "application/json");
    myHeader.append("Authorization", "Bearer " + $tokenStore);
    
    const formData = new FormData(event.target);
    
    var object = {};

    formData.forEach((value, key) => object[key] = value);
    object.cards = cards; 
    var data = JSON.stringify(object);
    console.log(object);

    
    const requestOptions = {
      method: method, 
      headers: myHeader,
      body: data,
    }

    let res = await fetch(action, requestOptions);
    res = await res.json();
    
    //if success reset form
    if(res.success){
      event.target.reset();
      cards = [];
    }
    
    addToastByRes(res);
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
  <form class="flex justify-center" method="POST" action="api/create-deck" on:submit|preventDefault={handleSubmit}>
    <div class="flex flex-col">
      <div class="form-control">
        <label class="input-group">
        <span class="w-36">Name</span>
        <input name="name" required type="text" placeholder="Softwarearchitecture" class="input input-bordered w-full bg-slate-900" />
        </label>
      </div>
      <br class="pt-4"/>
      <div class="form-control">
        <label class="input-group">
        <span class="w-36">Description</span>
        <textarea name="description" required placeholder="A deck to learn softwarearchitecture" class="textarea input-bordered w-full bg-slate-900" />
        </label>
      </div>
      <br class="pt-4"/>
      <div class="form-control">
        <label class="input-group">
        <span class="w-36">Publish</span>
        <select name="isPublished" class="flex input w-full bg-slate-900" required>
            <option selected value={false}>false</option>
            <option value={true}>true</option>
        </select>
        </label>
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
  </form>
  
  <br class="mt-4"/>
  <div class="grid grid-cols-4 gap-2">
    {#each cards as card}
      <DualSideCard {card} on:deleteCard={()=>handleDeleteCard(card)}/>
    {/each}
  </div>
</main>