<script lang="ts">
	import Nav from './lib/components/nav.svelte';
	import Deck from './lib/components/deck.svelte';
	import { redirect } from "./lib/utils/redirect";
	import { token } from "./lib/stores/token";
  	import { handleLogout } from './lib/utils/handleLogout';
  	import Modal from './lib/components/modal.svelte';

	$: if($token.length < 30) redirect("login");

	let showCreateDeckModal = false;
	let showEditDeckModal = false;
	let selectedDeck = null;

	$: console.log("showeditdeckmodal " + showEditDeckModal);
	$: console.log("selectedDeck " + {selectedDeck});

	//TODO only show admin button if user is admin
	let navButtons = [
		{ tag: "label", id: "createDeckModal", text: "Create Deck", action: () =>{showCreateDeckModal = true;} },
		{ tag: "button", id: "", text: "Admin", action: () => redirect("admin") },
		{ tag: "button", id: "", text: "Logout", action: () => handleLogout()}
	]
	
	function handleSubmit(event) {
		console.log(event);
	}
	$: console.log(showCreateDeckModal);

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
		<Modal uniqueModalQualifier="createDeckModal">
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
				<div class="flex justify-between">
					<button type="submit" class="btn btn-primary">Create</button>
					<input type="reset" class="btn btn-primary" value="Clear"/>
					<label for="createDeckModal" class="btn btn-primary" on:click={()=> showCreateDeckModal = false}>Close</label>
				</div>
			</div>
			</form>
		</Modal>
	{/if}

	{#if showEditDeckModal}
		<Modal uniqueModalQualifier="editDeckModal">
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
				<br class="pt-4"/>
				<div class="flex justify-between">
					<button type="submit" class="btn btn-primary">Update</button>
					<input type="reset" class="btn btn-primary" value="Clear"/>
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
