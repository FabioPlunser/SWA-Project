<script lang="ts">
	import Nav from './lib/components/nav.svelte';
	import Deck from './lib/components/deck.svelte';
	import Modal from './lib/components/modal.svelte';
	
	import { get } from 'svelte/store';
	import { redirect } from "./lib/utils/redirect";
	import { tokenStore } from "./lib/stores/tokenStore";
	import { userPermissionsStore } from './lib/stores/userPermissionsStore';
	import { personIdStore } from './lib/stores/peronsIdStore';
  	import { handleLogout } from './lib/utils/handleLogout';

	$: if($tokenStore.length < 30) redirect("login");
	$: personId = get(personIdStore);
	$: getDecks();
	let showCreateDeckModal = false;
	let showEditDeckModal = false;
	let selectedDeck = null;
	let disableModalClick = false;


	//TODO only show admin button if user is admin
	//TODO admin gets to see all the decks that user own. 
	let navButtons = [
		{ tag: "label", id: "createDeckModal", text: "Create Deck", action: () =>{showCreateDeckModal = true;} },
		{ tag: "button", id: "", text: "Logout", action: () => handleLogout()}
	]
	if ($userPermissionsStore.includes("ADMIN")) {
		navButtons.splice(1, 0, { tag: "button", id: "", text: "Admin", action: () => redirect("admin") });
	}
	
	async function getDecks(){
		const myHeaders = new Headers();
		myHeaders.append("Authorization", "Bearer " + $tokenStore);
		if($userPermissionsStore.includes('ADMIN')){
			let res = await fetch("/api/getAllDecks", {
				method: "GET",
				headers: myHeaders,
			});
			let data = await res.json();
			console.log(data);
		}
		else{
			let res = await fetch("/api/getUserDecks/?personId=" + personId, {
				method: "GET",
				headers: myHeaders,
			});
			let data = await res.json();
			console.log(data);
		}
	}
	function handleSubmit(event) {
		console.log(event);
	}

	let decks = [
		{ 
			id: 1,
			title: "Softwarearchitecture",
			description: "A deck to learn softwarearchitecture",
			numberOfCards: 12,
			numberOfCardsLearned: 4,
			numberOfCardsToLearn: 8,
			published: false,
		},
		{ 
			id: 2,
			title: "Databasesystems",
			description: "A deck to learn databasesystems",
			numberOfCards: 20,
			numberOfCardsLearned: 1,
			numberOfCardsToLearn: 20,
			published: false,
		},
		{ 
			id: 3,
			title: "Cisco",
			description: "A deck to learn cisco",
			numberOfCards: 100,
			numberOfCardsLearned: 50,
			numberOfCardsToLearn: 50,
			published: true,
		},
	]

	let newCards = [];
	function addToNewCards(){
		let obj = {id: 0, front: "", back: ""};
		newCards = [...newCards, obj];
	}
	//TODO get Decks from server 
	//TODO send edited decks to server
		
</script>

<svelte:head>
	<link rel="icon" type="image/png" href="/favicon.png"/>
	<title>Decks overview</title>
</svelte:head>

<Nav title="Decks overview" buttons={navButtons} />


<main class="m-20">
	{#if showCreateDeckModal}
		<Modal uniqueModalQualifier="createDeckModal" {disableModalClick} on:close={()=> showCreateDeckModal = false}>
			<h1 class="flex justify-center text-2xl underline">Create Deck</h1>
			<br class="pt-4"/>
			<form class="flex justify-center" method="POST" action="api/createDeck" on:submit|preventDefault={handleSubmit}>
			<input name="personId" type="hidden" required>
			<div class="flex flex-col">
				<div class="form-control">
					<label class="input-group">
					<span class="w-36">Title</span>
					<input name="Title" required type="text" placeholder="Softwarearchitecture" class="input input-bordered w-full" />
					</label>
				</div>
				<br class="pt-4"/>
				<div class="form-control">
					<label class="input-group">
					<span class="w-36">Description</span>
					<textarea name="description" required type="text" placeholder="A deck to learn softwarearchitecture" class="textarea input-bordered w-full" />
					</label>
				</div>
				<br class="pt-4"/>
				<div class="form-control">
					<label class="input-group">
					<span class="w-36">Publish</span>
					<select name="publish" class="flex input w-full" required>
							<option selected>False</option>
							<option>True</option>
					</select>
					</label>
				</div>
				<br class="pt-4"/>
				<h1 class="flex justify-center text-2xl underline">Cards</h1>
				<br class="pt-4"/>
				<!-- svelte-ignore a11y-mouse-events-have-key-events -->
				<div id="addCards" class="flex flex-col justify-center" on:mouseover={()=>disableModalClick = true}>
					{#each newCards as card}
						<div class="flex">
							<div class="form-control m-2">
								<label class="input-group">
								<span class="w-36">Front</span>
								<input bind:value={card.front} name="front" required type="text" placeholder="Front" class="input input-bordered w-full" />
								</label>
							</div>
							<br class="pt-4"/>
							<div class="form-control m-2">
								<label class="input-group">
								<span class="w-36">Back</span>
								<input bind:value={card.back} name="back" required type="text" placeholder="Back" class="input input-bordered w-full" />
								</label>
							</div>
						</div>
						<br class="pt-4"/>
					{/each}
					<div class="tooltip" data-tip="Add Card">
						<button class="btn btn-circle btn-accent" type="button" on:click={()=>{addToNewCards()}}>
							<svg xmlns="http://www.w3.org/2000/svg" class="h-6 w-6" fill="none" viewBox="0 0 24 24" stroke="currentColor"><path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M6 18L18 6M6 6l12 12" /></svg>
						</button>
					</div>
				</div>
				<br class="pt-4"/>
				<div class="flex justify-between">
					<button type="submit" class="btn btn-primary">Create</button>
					<input type="reset" class="btn btn-primary" value="Clear"/>
					<!-- svelte-ignore a11y-click-events-have-key-events -->
					<label for="createDeckModal" class="btn btn-primary" on:click={()=> showCreateDeckModal = false}>Close</label>
				</div>
			</div>
			</form>
		</Modal>
	{/if}

	{#if showEditDeckModal}
		<Modal uniqueModalQualifier="editDeckModal" on:close={()=> showEditDeckModal = false}>
			<h1 class="flex justify-center text-2xl underline">Edit Deck</h1>
			<br class="pt-4"/>
			<form class="flex justify-center" method="POST" action="api/editDeck" on:submit|preventDefault={handleSubmit}>
			<input name="personId" type="hidden" required>
			<div class="flex flex-col">
				<div class="form-control">
					<label class="input-group">
					<span class="w-36">Title</span>
					<input bind:value={selectedDeck.title} name="Title" required type="text" placeholder="Softwarearchitecture" class="input input-bordered w-full" />
					</label>
				</div>
				<br class="pt-4"/>
				<div class="form-control">
					<label class="input-group">
					<span class="w-36">Description</span>
					<textarea bind:value={selectedDeck.description} name="description" required type="text" placeholder="A deck to learn softwarearchitecture" class="textarea input-bordered w-full" />
					</label>
				</div>
				<br class="pt-2"/>
				<button class="btn btn-primary" on:click={()=> redirect("listcards")}>Edit Cards</button>
				<br class="pt-4"/>
				<div class="flex justify-between">
					<button type="submit" class="btn btn-primary">Update</button>
					<input type="reset" class="btn btn-primary" value="Clear"/>
					<!-- svelte-ignore a11y-click-events-have-key-events -->
					<label for="editDeckModal" class="btn btn-primary" on:click={()=> showEditDeckModal = false}>Close</label>
				</div>
			</div>
			</form>
		</Modal>
	{/if}
	<div class="grid grid-cols-4 gap-4">
		{#each  decks as deck}
			<Deck 
				title={deck.title} 
				description={deck.description} 
				learned={deck.numberOfCardsLearned} 
				NotLearned={deck.numberOfCardsToLearn} 
				published={deck.published}
				on:editDeck={()=> {selectedDeck = deck; showEditDeckModal = true}}
			/>
		{/each}
	</div>
</main>
