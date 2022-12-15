<script lang="ts">
  import favicon from "/favicon.png";
  import Nav from "../../lib/components/nav.svelte";
  import { redirect } from '../../lib/utils/redirect';
  import { handleLogout } from "../../lib/utils/handleLogout";
	import { adminSelectedDeckStore } from '../../lib/stores/adminSelectedDeckStore';
  import { tokenStore} from '../../lib/stores/tokenStore';
  let buttons = [
    { tag: "button", id: "", text: "Back", action: () => redirect("admin/showdecks") },
    { tag: "button", id: "", text: "Admin", action: () => redirect("admin") },
    { tag: "button", id: "", text: "Home", action: () => redirect("") },
    { tag: "button", id: "", text: "Logout",action: () => handleLogout()}
  ];

  $: console.log("showCards: ", $adminSelectedDeckStore);
  $: selectedDeck = $adminSelectedDeckStore;

  
</script>

<svelte:head>
  <title>Admin ShowCards</title>
  <link rel="icon" type="image/png" href="{favicon}" />
</svelte:head>

<Nav title="ShowCards" {buttons}/>
<main class="mt-20 m-2">
  <h1 class="flex justify-center text-2xl underline">Cards of Deck {selectedDeck.title}</h1>
  <br/>
  <div class="grid grid-cols-4 gap-4">
    {#each selectedDeck.cards as card}
      <div class="card bg-slate-900 rounded-xl shadow-xl">
        <div class="card-body">
          <div class="flex w-full">
            <div class="grid h-20 flex-grow card bg-base-300 rounded-box place-items-center">{card.front}</div>
            <div class="divider divider-horizontal"></div>
            <div class="grid h-20 flex-grow card bg-base-300 rounded-box place-items-center">{card.back}</div>
          </div>
        </div>
      </div>
    {/each}
  </div>
</main>