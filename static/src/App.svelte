<script lang="ts">
	import Nav from './lib/components/nav.svelte';
	import Deck from './lib/components/deck.svelte';
	import Modal from './lib/components/modal.svelte';
	import MediaQuery from './lib/utils/mediaQuery.svelte';
	import SvelteToast from './lib/components/SvelteToast.svelte';
	import { addToast, addToastByRes } from './lib/utils/addToStore';
	
	import { redirect } from "./lib/utils/redirect";
	import { tokenStore } from "./lib/stores/tokenStore";
	import { userPermissionsStore } from './lib/stores/userPermissionsStore';
  	import { handleLogout } from './lib/utils/handleLogout';
  	import { userSelectedDeckStore } from './lib/stores/userSelectedDeckStore';
  	import { personIdStore } from './lib/stores/personIdStore';
  import Spinner from './lib/components/Spinner.svelte';


	$: if($tokenStore.length < 30) redirect("login");
	$: getUserDecks();
	$: getPublicDecks();


	let showEditDeckModal = false;
	let selectedDeck = null;
	let showPublicDecks = false;
	let searchPublicDeckName = "";
	let selectedPublicDeck = false;

	let navButtons = [
		{ text: `Public Decks  <kbd class="ml-2 kbd">⌘+k</kbd>`, action: () => { showPublicDecks = true; getPublicDecks()}},
		{ text: "Create Deck", action: () => redirect("create-deck") },
		{ text: "Logout", action: () => handleLogout()}
	]

	if (navigator.userAgent.indexOf("Mac") != -1) {
		navButtons[0].text = `Public Decks  <kbd class="ml-2 kbd">⌘+k</kbd>`;
	}
	else{
		navButtons[0].text = `Public Decks  <kbd class="ml-2 kbd">Ctrl+K</kbd>`;
	}

	if ($userPermissionsStore.includes("ADMIN")) {
		navButtons.splice(2, 0, { text: "Admin", action: () => redirect("admin") });
	}

	const myHeaders = new Headers();
	myHeaders.append("Authorization", "Bearer " + $tokenStore);


	async function getAllDecks(){
		let res = await fetch("/api/get-all-decks", {
			method: "GET",
			headers: myHeaders,
		});
		res = await res.json();
		if(res.success) return res.items;
		else  addToast("Error fetching all decks", "alert-error");

	}

	async function getUserDecks(){
		let res = await fetch("/api/get-created-decks", {
			method: "GET",
			headers: myHeaders,
		});
		res = await res.json();
		if(res.success) return res.items;
		else addToast("Error fetching created decks", "alert-error");

	}

	async function getSubscribedDecks(){
		let res = await fetch("/api/get-subscribed-decks", {
			method: "GET",
			headers: myHeaders,
		});
		res = await res.json();
		if(res.success) return res.items;
		else {
			addToast("Error fetching subscribedDecks", "alert-error");
		}
	}

	async function getPublicDecks(){
		let res = await fetch("/api/get-published-decks", {
			method: "GET",
			headers: myHeaders,
		});
		res = await res.json();
		if(res.success) return res.items;
		else addToast("Error fetching publicDecks", "alert-error");
	}
	

	let allDecks = getAllDecks();
	let userDecks = getUserDecks();
	let subscribedDecks = getSubscribedDecks();
	let publicDecks = getPublicDecks();

	
	async function handleSubmit(event) {
		const action = event.target.action;
		const method = event.target.method.toUpperCase();
		const formData = new FormData(event.target);
		
		let res = await fetch(action, {
			method: method,
			headers: myHeaders,
			body: formData,
		});
		res = await res.json();
		addToastByRes(res);
	}

	async function handleSubscribe(deck){
		let res = await fetch(`/api/subscribe-deck?deckId=${deck.deckId}`, {
			method: "PUT",
			headers: myHeaders,
		});
		res = await res.json();
		addToastByRes(res);
	}

	async function handleUnsubscribe(deck){
		let res = await fetch(`/api/unsubscribe-deck?deckId=${deck.deckId}`, {
			method: "PUT",
			headers: myHeaders,
		});
		res = await res.json();
		addToastByRes(res);
	}

	function checkDeckInUserDeck(deck){
		let found = false;
		userDecks.then((decks) => {
			decks.forEach((userDeck) => {
				if(userDeck.deckId == deck.deckId){
					found = true;
				}
			});
		});
		return found;
	}
	$: {
		//set correct in userDeck array to selectedDeck
		if(selectedDeck != null){
			userDecks.forEach((deck, index) => {
				if(deck.deckId == selectedDeck.deckId){
					userDecks[index] = selectedDeck;
				}
			});
		}
	}

	let page = 'my-decks';
	$: console.log(userDecks)
</script>

<svelte:head>
	<link rel="icon" type="image/png" href="/favicon.png"/>
	<title>Decks overview</title>
</svelte:head>

<MediaQuery query="(min-width: 480px)" let:matches>
{#if matches}
<SvelteToast />
<Nav title="Decks overview" buttons={navButtons} />
<main class="m-20">
	{#if showEditDeckModal}
		<Modal open={showEditDeckModal} on:close={()=> showEditDeckModal = false} closeOnBodyClick={false}>
			<h1 class="flex justify-center text-2xl underline">Edit Deck</h1>
			<br class="pt-4"/>
			<form class="flex justify-center" method="POST" action="api/update-deck" on:submit|preventDefault={handleSubmit}>
				<input name="deckId" bind:value={selectedDeck.deckId} type="hidden" required>
				<div class="flex flex-col">
					<div class="form-control">
						<label class="input-group">
						<span class="w-36">Name</span>
						<input bind:value={selectedDeck.name} name="name" required type="text" placeholder="Softwarearchitecture" class="input input-bordered w-full" />
						</label>
					</div>
					<br class="pt-4"/>
					<div class="form-control">
						<label class="input-group">
						<span class="w-36">Description</span>
						<textarea bind:value={selectedDeck.description} name="description" required placeholder="A deck to learn softwarearchitecture" class="textarea input-bordered w-full" />
						</label>
					</div>
					<br class="pt-2"/>
					<button class="btn btn-primary" on:click={()=>{$userSelectedDeckStore = selectedDeck; redirect("list-cards")}}>Edit Cards</button>
					<br class="pt-4"/>
					<div class="flex justify-between">
						<button type="submit" class="btn btn-primary" on:click={()=>showEditDeckModal=false}>Update</button>
						<input type="reset" class="btn btn-primary" value="Clear"/>
						<!-- svelte-ignore a11y-click-events-have-key-events -->
						<button type="button" class="btn btn-primary" on:click={()=> showEditDeckModal = false}>Close</button>
					</div>
				</div>
			</form>
		</Modal>
	{/if}
	{#if showPublicDecks}
		<Modal open={showPublicDecks} on:close={()=>showPublicDecks=false} closeOnBodyClick={false}>
			<div class="flex flex-col min-w-fit">
				<h1 class="flex justify-center text-2xl underline">Public Decks</h1>
				<br class="mt-4"/>
				<input bind:value={searchPublicDeckName} placeholder="name" class="input w-full"/>
				<br class="mt-4"/>
				<div class="grid grid-cols-4 gap-2">
					{#await publicDecks}
						<Spinner/>
					{:then publicDecks}
						{#key publicDecks}
							{#each publicDecks as deck}
								<!-- Only show Decks that are not created by logged in User -->
								{#if checkDeckInUserDeck(deck)}
									{#if deck.name.includes(searchPublicDeckName) || deck.description.includes(searchPublicDeckName)} 
										<div class="card bg-gray-700 p-5 w-fit min-w-fit">
											<h1 class="card-title">{deck.name}</h1>
											<p class="card-subtitle">{deck.description}</p>
											{#if deck.subscribed}
												<button class="btn btn-secondary" on:click={()=> handleUnsubscribe(deck)}>Unsubscribe</button>
											{:else}
											<button class="btn btn-primary" on:click={()=> handleSubscribe(deck)}>Subscribe</button>
											{/if}
										</div>
									{/if}	
								{/if}
							{/each}
						{/key}
					{/await}
				</div>
			</div>

			<div class="modal-action">
				<button class="fixed btn btn-primary bottom-0 right-0 m-2 mt-12" on:click={()=> showPublicDecks = false}>Close</button>
			</div>
		</Modal>
	{/if}


	{#if $userPermissionsStore.includes('ADMIN')}
		<div class="btn-group flex justify-center mb-8">
			<button class="btn {page=="my-decks" ? "btn-active" : ""}" on:click={()=>page="my-decks"}>My Decks</button>
			<button class="btn {page=="all-decks" ? "btn-active" : ""}" on:click={()=>page="all-decks"}>All Decks</button>
		</div>
	{/if}

	{#if page == "my-decks"}
		<div>
			<h1 class="text-4xl underline flex justify-center m-2">MyDecks</h1>
			<div class="grid grid-cols-4 gap-4">
				{#await userDecks}
					<Spinner />
				{:then userDecks}
					{#key userDecks}
						{#each userDecks as deck}
						<Deck 
							{deck}
							on:editDeck={()=> {selectedDeck = deck; showEditDeckModal = true}}
							on:learnDeck={()=> {$userSelectedDeckStore = deck; redirect("learn")}}
							on:listCards={()=> {$userSelectedDeckStore = deck; redirect("list-cards")}}
							on:deleteDeck={()=> getUserDecks()}
						/>
						{/each}
					{/key}
				{/await}
			</div>
		</div>

		<div>
			<h1 class="text-4xl underline flex justify-center m-2">Subscribed Decks</h1>
			<div class="grid grid-cols-4 gap-4">
				{#await subscribedDecks}
					<Spinner />
				{:then subscribedDecks}
					{#key subscribedDecks}
						{#each subscribedDecks as deck}
						<Deck 
							{deck}
							on:editDeck={()=> {selectedDeck = deck; showEditDeckModal = true}}
							on:learnDeck={()=> {$userSelectedDeckStore = deck; redirect("learn")}}
							on:listCards={()=> {$userSelectedDeckStore = deck; redirect("list-cards")}}
							on:deleteDeck={()=> getSubscribedDecks()}
						/>
						{/each}
					{/key}
				{/await}
			</div>	
		</div>
	{/if}

	{#if page == "all-decks"}
		<div>
			<div class="grid grid-cols-4 gap-4">
			{#await allDecks}
				<Spinner />
			{:then allDecks}
				{#key allDecks}
					{#each allDecks as deck}
					<Deck {deck}/>
					{/each}
				{/key}
			{/await}
			</div>
		</div>
	{/if}
</main>
{/if}
</MediaQuery>