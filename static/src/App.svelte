<script lang="ts">
	import Nav from './lib/components/nav.svelte';
	import Deck from './lib/components/deck.svelte';
	import Modal from './lib/components/modal.svelte';
	import MediaQuery from './lib/utils/mediaQuery.svelte';
	import SvelteToast from './lib/components/SvelteToast.svelte';
	import { addToast } from './lib/utils/addToStore';
	
	import { redirect } from "./lib/utils/redirect";
	import { tokenStore } from "./lib/stores/tokenStore";
	import { userPermissionsStore } from './lib/stores/userPermissionsStore';
  	import { handleLogout } from './lib/utils/handleLogout';
  	import { userSelectedDeckStore } from './lib/stores/userSelectedDeckStore';

	$: if($tokenStore.length < 30) redirect("login");

	let showEditDeckModal = false;
	let selectedDeck = null;
	let showPublicDecks = false;
	let searchPublicDeckTitle = "";
	let publicDecks = [];

	let navButtons = [
		{ text: "Public Decks", action: () => { showPublicDecks = true; getPublicDecks()}},
		{ text: "Create Deck", action: () => redirect("createDeck") },
		{ text: "Logout", action: () => handleLogout()}
	]
	if ($userPermissionsStore.includes("ADMIN")) {
		navButtons.splice(1, 0, { text: "Admin", action: () => redirect("admin") });
	}
	
	async function getUserDecks(){
		//TODO get user decks
		// const myHeaders = new Headers();
		// myHeaders.append("Authorization", "Bearer " + $tokenStore);
		// if($userPermissionsStore.includes('ADMIN')){
		// 	let res = await fetch("/api/getAllDecks", {
		// 		method: "GET",
		// 		headers: myHeaders,
		// 	});
		// 	let data = await res.json();
		// 	console.log(data);
		// }
		// else{
		// 	let res = await fetch("/api/getUserDecks/?personId=" + personId, {
		// 		method: "GET",
		// 		headers: myHeaders,
		// 	});
		// 	let data = await res.json();
		// 	console.log(data);
		// }
	}

	async function getPublicDecks(){
		// TODO get public decks
	}
	function handleSubmit(event) {
		const action = event.target.action;
		const method = event.target.method.toUpperCase();

		console.log(event);

		addToast(method + " " + action.split("/").pop() + " Failed", "alert-error");
	}

	let privateDecks = [
		{ 
			id: 1,
			title: "Softwarearchitecture",
			description: "A deck to learn softwarearchitecture",
			numberOfCardsLearned: 4,
			numberOfCardsToLearn: 8,
			published: false,
			cards: [
				{ id: 1, question: "Deck1 Question1", answer: "Deck1 Answer1" },
				{ id: 2, question: "Deck1 Question2", answer: "Deck1 Answer2" },
				{ id: 3, question: "Deck1 Question3", answer: "Deck1 Answer3" },
				{ id: 4, question: "Deck1 Question4", answer: "Deck1 Answer4" },
				{ id: 5, question: "Deck1 Question5", answer: "Deck1 Answer5" },
				{ id: 6, question: "Deck1 Question6", answer: "Deck1 Answer6" },
				{ id: 7, question: "Deck1 Question7", answer: "Deck1 Answer7" },
				{ id: 8, question: "Deck1 Question8", answer: "Deck1 Answer8" },
			]
		},
		{ 
			id: 2,
			title: "Databasesystems",
			description: "A deck to learn databasesystems",
			numberOfCardsLearned: 1,
			numberOfCardsToLearn: 20,
			published: false,
			cards: [
				{ id: 1, question: "Deck2 Question1", answer: "Deck2 Answer1" },
				{ id: 2, question: "Deck2 Question2", answer: "Deck2 Answer2" },
				{ id: 3, question: "Deck2 Question3", answer: "Deck2 Answer3" },
				{ id: 4, question: "Deck2 Question4", answer: "Deck2 Answer4" },
				{ id: 5, question: "Deck2 Question5", answer: "Deck2 Answer5" },
				{ id: 6, question: "Deck2 Question6", answer: "Deck2 Answer6" },
				{ id: 7, question: "Deck2 Question7", answer: "Deck2 Answer7" },
				{ id: 8, question: "Deck2 Question8", answer: "Deck2 Answer8" },
				{ id: 9, question: "Deck2 Question9", answer: "Deck2 Answer9" },
				{ id: 10, question: "Deck2 Question10", answer: "Deck2 Answer10" },
				{ id: 11, question: "Deck2 Question11", answer: "Deck2 Answer11" },
				{ id: 12, question: "Deck2 Question12", answer: "Deck2 Answer12" },
				{ id: 13, question: "Deck2 Question13", answer: "Deck2 Answer13" },
				{ id: 14, question: "Deck2 Question14", answer: "Deck2 Answer14" },
				{ id: 15, question: "Deck2 Question15", answer: "Deck2 Answer15" },
				{ id: 16, question: "Deck2 Question16", answer: "Deck2 Answer16" },
				{ id: 17, question: "Deck2 Question17", answer: "Deck2 Answer17" },
				{ id: 18, question: "Deck2 Question18", answer: "Deck2 Answer18" },
				{ id: 19, question: "Deck2 Question19", answer: "Deck2 Answer19" },
			]
		},
		{ 
			id: 3,
			title: "Cisco",
			description: "A deck to learn cisco",
			numberOfCardsLearned: 1,
			numberOfCardsToLearn: 4,
			published: true,
			cards: [
				{ id: 1, question: "Deck3 Question1", answer: "Deck3 Answer1" },
				{ id: 2, question: "Deck3 Question2", answer: "Deck3 Answer2" },
				{ id: 3, question: "Deck3 Question3", answer: "Deck3 Answer3" },
				{ id: 4, question: "Deck3 Question4", answer: "Deck3 Answer4" },
			]
		},
	]

	privateDecks.sort((a, b) => (a.numberOfCardsLearned > b.numberOfCardsLearned) ? 1 : -1)
	publicDecks = privateDecks.filter(deck => deck.published);
	//TODO get Decks from server 
	//TODO send edited decks to server
	
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
					<button class="btn btn-primary" on:click={()=>{$userSelectedDeckStore = selectedDeck; redirect("listcards")}}>Edit Cards</button>
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
		<Modal open={showPublicDecks} on:close={()=>showPublicDecks=false} closeOnBodyClick={false} >
			<div class="flex flex-col">
				<h1 class="flex justify-center text-2xl underline">Public Decks</h1>
				<br class="mt-4"/>
				<input bind:value={searchPublicDeckTitle} placeholder="title" class="input w-full"/>
				<br class="mt-4"/>
				<div class="grid grid-cols-2 gap-2">
					{#each publicDecks as deck}
						{#if deck.title.includes(searchPublicDeckTitle)}
							<div class="card bg-gray-600 p-5 w-auto">
								<h1 class="card-title">{deck.title}</h1>
								<p class="card-subtitle">{deck.description}</p>
								{#if privateDecks.includes(deck)}
									<button class="btn btn-secondary">Unsubscribe</button>
								{:else}
								<button class="btn btn-primary">Subscribe</button>
								{/if}
							</div>
						{/if}
					{/each}
				</div>
			</div>
			<button class="fixed btn btn-primary bottom-0 right-0 m-2" on:click={()=> showPublicDecks = false}>Close</button>
		</Modal>
	{/if}
	<div class="grid grid-cols-4 gap-4">
		{#each privateDecks as deck}
			<Deck 
				{deck}
				on:editDeck={()=> {selectedDeck = deck; showEditDeckModal = true}}
				on:learnDeck={()=> {$userSelectedDeckStore = deck; redirect("learn")}}
				on:listCards={()=> {$userSelectedDeckStore = deck; redirect("listcards")}}
			/>
		{/each}
	</div>
</main>
{/if}
</MediaQuery>