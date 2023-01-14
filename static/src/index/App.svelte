<script lang="ts">
	import Nav from '$components/nav.svelte';
	import SvelteToast from '$components/SvelteToast.svelte';

	import Deck from '$components/deck.svelte';
	import AdminDeck from '$components/adminDeck.svelte';
	import SubscribedDeck from '$components/subscribedDeck.svelte';
	import Modal from '$components/modal.svelte';
	import Spinner from '$components/Spinner.svelte';
	import Form from '$components/Form.svelte';
	import DualSideCard from '$components/dualSideCard.svelte';
	import EditDeckModal from './editDeckModal.svelte';


	import { fly } from 'svelte/transition';

	import { jwt } from "$stores/jwtStore";
	import { addToast, addToastByRes } from '$utils/addToToastStore';
	import { redirect } from "$utils/redirect";
	import { userPermissionsStore } from '$stores/userPermissionsStore';
	import { userSelectedDeckStore } from '$stores/userSelectedDeckStore';
  	import { fetching } from '$utils/fetching';

	$: if(!$jwt || $jwt?.expired) redirect("login");
	$: if($userPermissionsStore.includes("ADMIN")) getAllDecks();
	$: getUserDecks();
	$: getSubscribedDecks();
	$: getPublicDecks();

	let allDecks = [];
	let userDecks = [];
	let subscribedDecks = [];
	let publicDecks = [];

	$: console.log(userDecks);

	let showEditDeckModal = false;
	let selectedDeck: IDeck = null;
	let listCards = false;
	
	let showPublicDecks = false;
	let selectedPublicDeck = null;
	let searchPublicDeckName = "";
	let searchDeck = "";
	let showMyDecks = true;
	let showSubscribedDecks = true;

	let adminShowBlockedDecks = true;
	let adminShowDeletedDecks = true;
	let adminSearch = "";
	let page = 'my-decks';

	let navButtons = [
		{ text: `Public Decks  <kbd class="ml-2 kbd">⌘+k</kbd>`, action: ()=>{showPublicDecks=true; getPublicDecks()}},
		{ text: "Create Deck", href: "create-deck" },
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

	
	async function getDecks(){
		if($userPermissionsStore.includes("ADMIN")){
			allDecks = await getAllDecks();
		}
		getUserDecks();
		getSubscribedDecks();
		getPublicDecks();
	}


	async function handleSubscribe(deck){
		let res = await fetching(`/api/subscribe-deck`, "POST", [{name: "deckId", value: deck.deckId}]);
		addToastByRes(res);
		getSubscribedDecks();
		getPublicDecks();
	}
	
	async function getCardsFromDeck(deck){
        let res = await fetching("/api/get-cards-of-deck", "GET", [{name: "deckId", value: deck.deckId}]);
        return res.items;
    }
</script>

<svelte:head>
	<link rel="icon" type="image/png" href="/favicon.png"/>
	<title>Decks overview</title>
    <script src="http://localhost:35729/livereload.js"></script>
</svelte:head>

<SvelteToast />
<Nav title="Decks" buttons={navButtons} />
<main class="m-20">
	{#if showEditDeckModal}
		<Modal open={showEditDeckModal} on:close={()=> showEditDeckModal = false} closeOnBodyClick={false}>
			<div class="max-w-full">
				<h1 class="flex justify-center text-2xl font-bold">Edit Deck</h1>
				<br class="pt-4"/>
				<Form url="/api/update-deck" method="POST" dataFormat="JSON" on:postFetch={getDecks}>
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
						<button class="btn btn-primary" type="button" on:click={()=>{$userSelectedDeckStore = selectedDeck; redirect("edit-cards")}}>Edit Cards</button>
						<br class="pt-4"/>
						<div class="flex justify-between">
							<button type="submit" class="btn btn-primary" on:click={()=>showEditDeckModal=false}>Update</button>
							<input type="reset" class="btn btn-primary" value="Clear"/>
							<!-- svelte-ignore a11y-click-events-have-key-events -->
							<button type="button" class="btn btn-primary" on:click={()=> showEditDeckModal = false}>Close</button>
						</div>
					</div>
					
				</Form>
			</div>
		</Modal>
	{/if}
	{#if showPublicDecks}
		<Modal open={showPublicDecks} on:close={()=>showPublicDecks=false} closeOnBodyClick={false}>
			<div class="flex flex-col min-w-fit">
				<h1 class="flex justify-center text-2xl font-bold">Public Decks</h1>
				<br class="mt-4"/>
				<input bind:value={searchPublicDeckName} placeholder="name" class="input w-full"/>
				<br class="mt-4"/>
				{#await publicDecks}
					<Spinner/>
				{:then publicDecks}
					{#if publicDecks.length == 0}
						<h1 class="text-2xl flex justify-center">No Decks Found</h1>
					{:else}
						<div class="grid grid-cols-4 gap-2">
							{#each publicDecks as deck}
								{#if deck.name.toLowerCase().includes(searchPublicDeckName.toLowerCase()) || deck.description.toLowerCase().includes(searchPublicDeckName.toLowerCase())} 
									<div class="card bg-gray-700 p-5 w-fit min-w-fit">
										<h1 class="card-title">{deck.name}</h1>
										<p class="card-subtitle">{deck.description}</p>
										{#if deck.numCards > 0}
											<div class="badge badge-primary">Number of cards: {deck.numCards} </div>
											{:else}
											<div class="badge badge-error">No cards</div>
										{/if}
										<br class="pt-4"/>
										<div class="card-actions">
											<button class="btn btn-primary" on:click={()=> {handleSubscribe(deck)}}>Subscribe</button>
											<button class="btn btn-secondary" on:click={()=> {showPublicDecks=false; selectedDeck=deck; listCards=true}}>Cards</button>
										</div>
									</div>
								{:else}
									<h1 class="flex justify-center text-3xl">No deck found</h1>
								{/if}
							{/each}
						</div>
					{/if}
				{/await}
			</div>
			<div class="mt-12 modal-action">
				<div class="flex fixed bottom-0 right-0 m-2">
					{#if selectedPublicDeck}
						<button class="btn btn-secondary m-1" on:click={()=> selectedPublicDeck=null}>Back</button>
					{/if}
					<button class="btn btn-primary m-1" on:click={()=> showPublicDecks = false}>Close</button>
				</div>
			</div>
		</Modal>
	{/if}
	{#if listCards}
		<Modal open={listCards} on:close={()=>listCards=false} closeOnBodyClick={true}>
			<div class="relative max-w-full">
				<div class="flex flex-col min-w-fit">
					<h1 class="flex justify-center text-2xl font-bold">Cards of Deck {selectedDeck.name}</h1>
					<div class="absolute right-0">
						<button class="btn btn-primary" on:click={()=> listCards = false}>Close</button>
					</div>
					<div class="grid grid-cols-4 gap-2 mt-6">
						{#await getCardsFromDeck(selectedDeck)}
							<Spinner/>
						{:then cards}
							{#each cards as card, index (card.cardId)}
								<div>
									<DualSideCard {card} {index} editable={false} cardBg="bg-slate-800" textBg="bg-slate-700"/>
								</div>
							{/each}
						{/await}
					</div>
				</div>
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
		<div class="flex justify-center">
			<div class="flex justify-center mx-auto w-full">
				<label class="input-group flex justify-center w-full ">
					<span><i class="bi bi-search"/></span>
					<input type="text" placeholder="Search for Deck" class="input input-bordered bg-slate-900" bind:value={searchDeck}/>
				</label>
			</div>
		</div>
		<div>
			<div class="flex">
				<h1 class="text-4xl font-bold m-2">My Decks</h1>
				<div class="flex items-center">
					<input type="checkbox" class="flex items-center toggle toggle-info" bind:checked={showMyDecks} />
				</div>
			</div>
			{#if showMyDecks}
			<div>
				{#if userDecks.length == 0}
					<h1 class="text-2xl m-2">No Decks Found</h1>
				{:else}
					<div class="grid grid-cols-4 gap-4">
							{#each userDecks as deck (deck.deckId)}
								{#if deck.name.includes(searchDeck) && deck.name.includes(searchDeck)}
									<div in:fly={{y: -100, duration: 300}} out:fly={{y: 100, duration: 300}}>
										<Deck 
											{deck}
											on:editDeck={()=> {selectedDeck = deck; showEditDeckModal = true}}
											on:learnDeck={()=> {$userSelectedDeckStore = deck; redirect("learn")}}
											on:listCards={()=> {listCards=true; selectedDeck = deck}}
											on:deleteDeck={async ()=> {await getUserDecks(); userDecks=[...userDecks];}}
										/>
									</div>
								{:else}
									<h1>No deck found</h1>
								{/if}
							{/each}
					</div>
				{/if}
			</div>
			{/if}
		</div>

		<div>
			<div class="flex">
				<h1 class="text-4xl font-bold m-2">Subscribed Decks</h1>
				<div class="flex items-center">
					<input type="checkbox" class="flex items-center toggle toggle-info" bind:checked={showSubscribedDecks} />
				</div>
			</div>
			{#if showSubscribedDecks}
			<div>
				{#if subscribedDecks.length == 0}
					<h1 class="text-2xl m-2">No subscribed Decks</h1>
				{:else}
					<div class="grid grid-cols-4 gap-4">
						{#each subscribedDecks as deck (deck.deckId)}
							{#if deck.name.includes(searchDeck) && deck.name.includes(searchDeck)}
								<div in:fly={{y: -100, duration: 300}} out:fly={{y: 100, duration: 300}}>
									<SubscribedDeck
										{deck}
										on:learnDeck={()=> {$userSelectedDeckStore = deck; redirect("learn")}}
										on:listCards={()=> {listCards=true; selectedDeck = deck}}
										on:unsubscribe={()=>getSubscribedDecks()}										
									/>
								</div>
								{:else}
								<h1>No deck found</h1>
							{/if}
						{/each}
					</div>	
				{/if}
			</div>
			{/if}
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
				<label class="input-group flex justify-center w-full ">
				<span><i class="bi bi-search"/></span>
				<input type="text" placeholder="Search for Deck" class="input input-bordered bg-slate-900" bind:value={adminSearch}/>
				</label>
			</div>

		<br class="mt-4"/>


		<div>
			{#await getAllDecks()}
				<Spinner/>
			{:then allDecks}
				{#if allDecks.length == 0}
					<h1 class="text-2xl font-bold flex justify-center">No Decks</h1>
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
								{:else}
									<h1>No deck found</h1>
								{/if}
							{/each}
						</div>
					{/key}
				{/if}
			{/await}
		</div>
	{/if}
</main>