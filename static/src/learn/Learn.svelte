<script lang="ts">
    import FlipCard from "../lib/components/flipCard.svelte";
	import Nav from "../lib/components/nav.svelte";


	import { redirect } from '../lib/utils/redirect';
    import { tokenStore } from "../lib/stores/tokenStore";
	import { handleLogout } from '../lib/utils/handleLogout';
	import { userSelectedDeckStore } from '../lib/stores/userSelectedDeckStore';
	import { fetching } from '../lib/utils/fetching';
   
	$: if($tokenStore.length < 30) redirect("login");
	$: getAllCardsToLearn();


	let buttons = [
		{ tag: "button", id: "", text: "DeckView", action: () => redirect("") },
		{ tag: "button", id: "", text: "Logout", action: handleLogout }
	];

	let cards = [];
	let card;
	
	async function getAllCardsToLearn(){
		let res = await fetching("/api/get-all-cards-to-learn", "GET", [{deckId: $userSelectedDeckStore.deckId}]);
		cards = res.items;	
		console.log(cards);
	}

	function nextCard(){
		cards.shift();
		card = cards[0];
	}
	
</script>

<svelte:head>
    <title>Learn View</title>
    <link rel="icon" type="image/gif" href="https://media.giphy.com/avatars/KirstenHurley/kdEReo8fnjUs/200h.gif"/>
</svelte:head>


<Nav title="LearnView" buttons={buttons}/>
<main class="mt-20">
	<div class="grid grid-row gap-6 justify-center">
		<FlipCard {card}/>
		<div class="grid grid-cols-6 gap-4">
			<div class="tooltip tooltip-error" data-tip="Keine Ahnung; totales Blackout">
				<button class="btn btn-error" on:click={nextCard}>0</button>
			</div>
			<div class="tooltip tooltip-warning" data-tip="Falsche Antwort, und nach Anzeige der Rückseite wäre die Karte schwer richtig zu 
			beantworten gewesen">
				<button class="btn btn-warning" on:click={nextCard}>1</button>
			</div>
			<div class="tooltip tooltip-secondary" data-tip="Falsche Antwort, aber nach Anzeige der Rückseite wäre die Karte leicht richtig zu 
			beantworten gewesen">
				<button class="btn btn-secondary" on:click={nextCard}>2</button>
			</div>
			<div class="tooltip tooltip-primary" data-tip="Richtige Antwort nach angestrengtem Nachdenken">
				<button class="btn btn-primary" on:click={nextCard}>3</button>
			</div>
			<div class="tooltip tooltip-info" data-tip="Richtige Antwort nach einer kurzen Pause">
				<button class="btn btn-info" on:click={nextCard}>4</button>
			</div>
			<div class="tooltip tooltip-success" data-tip="Richtige Antwort ohne langes Zögern">
				<button class="btn btn-success" on:click={nextCard}>5</button>
			</div>
		</div>
	</div>
</main>

