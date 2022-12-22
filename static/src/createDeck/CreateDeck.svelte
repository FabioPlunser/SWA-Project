<script lang="ts">
	import favicon from '/favicon.png';
  import Nav from '../lib/components/nav.svelte';

  import { redirect } from "../lib/utils/redirect";
  import {handleLogout} from '../lib/utils/handleLogout';

  import { tokenStore } from "../lib/stores/tokenStore";


  let buttons = [
    { text: "Home", action: () => redirect("") },
    { text: "Logout", action: () => handleLogout()}
  ]

  let newCards = [];
  let id = 0;
  function addCard() {
    newCards.push({id: id, front: "", back: ""});
    newCards = [...newCards];
    id++;
  }

  function handleDeleteCard(card) {
    console.log(card);
    newCards = newCards.filter(c => c.id !== card.id);
    newCards = [...newCards];

  }

  async function handleSubmit(event) {
    console.log(event);
  }

</script>

<svelte:head>
  <title>Create Deck</title>
  <link rel="icon" type="image/png" href={favicon}>
</svelte:head>


<Nav title="Decks" {buttons}/>
<main class="mt-20 m-2">
  <h1 class="flex justify-center text-2xl underline">Create Deck</h1>
  <br class="pt-4"/>
  <form class="flex justify-center" method="POST" action="api/createDeck" on:submit|preventDefault={handleSubmit}>
    <input name="personId" type="hidden" required>
    <div class="flex flex-col">
      <div class="form-control">
        <label class="input-group">
        <span class="w-36">Title</span>
        <input name="Title" required type="text" placeholder="Softwarearchitecture" class="input input-bordered w-full bg-slate-900" />
        </label>
      </div>
      <br class="pt-4"/>
      <div class="form-control">
        <label class="input-group">
        <span class="w-36">Description</span>
        <textarea name="description" required type="text" placeholder="A deck to learn softwarearchitecture" class="textarea input-bordered w-full bg-slate-900" />
        </label>
      </div>
      <br class="pt-4"/>
      <div class="form-control">
        <label class="input-group">
        <span class="w-36">Publish</span>
        <select name="publish" class="flex input w-full bg-slate-900" required>
            <option selected>False</option>
            <option>True</option>
        </select>
        </label>
      </div>
      <br class="pt-4"/>
      <h1 class="flex justify-center text-2xl underline">Cards</h1>
      <br class="pt-4"/>
      <div class="tooltip" data-tip="Add Card">
        <button class="btn btn-accent" type="button" on:click={()=>{addCard()}}>Add Card</button>
      </div>
      <input bind:value={newCards} name="cards" type="hidden" required>
    </div>
  </form>
  
  <br class="mt-4"/>
  <div class="grid grid-cols-4 gap-2">
    {#each newCards as card}
      <div class="card bg-slate-800 p-5">
        <h1 class="flex justify-center text-xl">Card {card.id}</h1>
        <div class="flex flex-row w-auto p-2">
          <textarea name="question" required type="text" placeholder="question" class="input input-bordered w-full bg-slate-900" />
          <div class="divider divider-horizontal"></div> 
          <textarea name="answer" required type="text" placeholder="answer" class="input input-bordered w-full bg-slate-900" />
        </div>
        <div class="flex justify-center">
          <button class="btn btn-accent" type="button" on:click={()=>handleDeleteCard(card)}>Delete Card</button>
        </div>
      </div>
    {/each}
  </div>
</main>