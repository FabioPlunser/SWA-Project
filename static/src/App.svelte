<script lang="ts">
	import Nav from './lib/components/nav.svelte';
	import MediaQuery from './lib/utils/mediaQuery.svelte';
	import SvelteToast from './lib/components/SvelteToast.svelte';
	
	import Deck from './lib/components/deck.svelte';
	import AdminDeck from './lib/components/adminDeck.svelte';
	import Modal from './lib/components/modal.svelte';
	import Spinner from './lib/components/Spinner.svelte';
	
	import { addToast, addToastByRes } from './lib/utils/addToToastStore';
	import { redirect } from "./lib/utils/redirect";
	import { tokenStore } from "./lib/stores/tokenStore";
	import { userPermissionsStore } from './lib/stores/userPermissionsStore';
	import { handleLogout } from './lib/utils/handleLogout';
	import { userSelectedDeckStore } from './lib/stores/userSelectedDeckStore.ts';
  	import { formFetch, fetching } from './lib/utils/fetching';


	$: if($tokenStore.length < 30) redirect("login");
	$: if($userPermissionsStore.includes("ADMIN")) getAllDecks();
	$: getUserDecks();
	$: getSubscribedDecks();
	$: getPublicDecks();

	let allDecks = [];

	let showEditDeckModal = false;
	let selectedDeck = null;
	let showPublicDecks = false;
	let searchPublicDeckName = "";

	let adminShowBlockedDecks = true;
	let adminShowDeletedDecks = true;
	let adminSearch = "";
	let page = 'my-decks';

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
		let res = await fetching("/api/get-all-decks", "GET");
		if(res.success){
			allDecks = res.items;
			return res.items;
		} 
		else{
			addToast("Error fetching all decks", "alert-error");
		}
	}
	
 

	async function getUserDecks(){
		let res = await fetching("/api/get-created-decks", "GET");
		if(res.success){
			userDecks = res.items;
			return res.items;
		} 
		else{
			addToast("Error fetching created decks", "alert-error");
		} 

	}
	let userDecks = getUserDecks();


	async function getSubscribedDecks(){
		let res = await fetching("/api/get-subscribed-decks", "GET");
		if(res.success){
			subscribedDecks = res.items;
			return res.items;
		} 
		else{
			addToast("Error fetching subscribed decks", "alert-error");
		}
	}
	let subscribedDecks = getSubscribedDecks();

	async function getPublicDecks(){
		let res = await fetching("/api/get-published-decks", "GET");
		if(res.success){
			publicDecks = res.items;
			return res.items;
		} 
		else{
			addToast("Error fetching public decks", "alert-error");
		}
	}
	let publicDecks = getPublicDecks();
	
	async function handleSubmit(event) {		
		let res = await formFetch(event, true);
		console.log(res);
		addToastByRes(res);
		
		userDecks = getUserDecks();
		subscribedDecks =  getSubscribedDecks();
		publicDecks = getPublicDecks();
	}

	async function handleSubscribe(deck){
		await fetching(`/api/subscribe-deck`, "POST", {deckId: deck.deckId});
	}

	async function handleUnsubscribe(deck){
		await fetching(`/api/unsubscribe-deck`, "POST", {deckId: deck.deckId}); 
	}

	function checkDeckInUserDeck(deck){
		let found = false;
		userDecks.forEach(userDeck => {
			if(userDeck.deckId == deck.deckId){
				found = true;
			}
		});
		return found;
	}
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
				{#await publicDecks}
					<Spinner/>
				{:then publicDecks}
					{#if publicDecks.length == 0}
						<h1 class="text-2xl flex justify-center">No Decks Found</h1>
					{:else}
						{#key publicDecks}
							<div class="grid grid-cols-4 gap-2">
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
							</div>
						{/key}
					{/if}
				{/await}
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
				{#await userDecks}
						<Spinner />
				{:then userDecks}
					{#if userDecks.length == 0}
						<h1 class="text-2xl flex justify-center">No Decks Found</h1>
					{:else}
						<div class="grid grid-cols-4 gap-4">
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
						</div>
					{/if}
				{/await}
		</div>

		<div>
			<h1 class="text-4xl underline flex justify-center m-2">Subscribed Decks</h1>
				{#await subscribedDecks}
						<Spinner />
				{:then subscribedDecks}
					{#if subscribedDecks.length == 0}
						<h1 class="flex justify-center">No subscribed Decks</h1>
					{:else}
						{#key subscribedDecks}
							<div class="grid grid-cols-4 gap-4">
								{#each subscribedDecks as deck}
								<Deck 
									{deck}
									on:editDeck={()=> {selectedDeck = deck; showEditDeckModal = true}}
									on:learnDeck={()=> {$userSelectedDeckStore = deck; redirect("learn")}}
									on:listCards={()=> {$userSelectedDeckStore = deck; redirect("list-cards")}}
									on:deleteDeck={()=> getSubscribedDecks()}
								/>
								{/each}
							</div>	
						{/key}
					{/if}
				{/await}
		</div>
	{/if}

	{#if page == "all-decks" && $userPermissionsStore.includes('ADMIN')}
			<div class="flex flex-row justify-center">
				<div class="form-control w-auto">
					<label class="cursor-pointer label">
					<h1 class="text-xl mx-2">Show blocked Decks:</h1> 
					<input type="checkbox" class="toggle toggle-info toggle-lg" bind:checked={adminShowBlockedDecks} />
					</label>
				</div>
				<div class="form-control w-auto">
					<label class="cursor-pointer label">
					<h1 class="text-xl mx-2">Show deleted Decks:</h1> 
					<input type="checkbox" class="toggle toggle-info toggle-lg" bind:checked={adminShowDeletedDecks}  />
					</label>
				</div>
				
			</div>
			
			<div class="flex justify-center mx-auto w-full">
				<label class="input-group flex justify-center w-full">
				<span><svg xmlns="http://www.w3.org/2000/svg" width="16" height="16" fill="currentColor" class="bi bi-search" viewBox="0 0 16 16">
					<path d="M11.742 10.344a6.5 6.5 0 1 0-1.397 1.398h-.001c.03.04.062.078.098.115l3.85 3.85a1 1 0 0 0 1.415-1.414l-3.85-3.85a1.007 1.007 0 0 0-.115-.1zM12 6.5a5.5 5.5 0 1 1-11 0 5.5 5.5 0 0 1 11 0z"/>
				  </svg></span>
				<input type="text" placeholder="Search for word in Name or Description" class="input input-bordered" bind:value={adminSearch}/>
				</label>
			</div>

		<br class="mt-4"/>


		<div>
			{#await getAllDecks()}
				<Spinner/>
			{:then allDecks}
				{#if allDecks.length == 0}
					<h1 class="flex justify-center">No Decks</h1>
				{:else}
					{#key allDecks}
						<div class="grid grid-cols-4 gap-4">
							{#each allDecks as deck}
								{#if deck.name.includes(adminSearch) || deck.description.includes(adminSearch)}
									{#if adminShowBlockedDecks && deck.blocked}
										<AdminDeck {deck}/>
									{:else if adminShowDeletedDecks && deck.deleted}
										<AdminDeck {deck}/>
									{:else if !deck.blocked && !deck.deleted}
										<AdminDeck {deck}/>
									{/if}
								{/if}
							{/each}
						</div>
					{/key}
				{/if}
			{/await}
		</div>
	{/if}
</main>
{/if}
</MediaQuery>