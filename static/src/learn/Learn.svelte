<script lang="ts">
    import FlipCard from "../lib/components/flipCard.svelte";
	import Nav from "../lib/components/nav.svelte";
	import SvelteToast from "../lib/components/SvelteToast.svelte";
	import { fly, fade } from 'svelte/transition';
	
	import { redirect } from '../lib/utils/redirect';
    import { tokenStore } from "../lib/stores/tokenStore";
	import { handleLogout } from '../lib/utils/handleLogout';
	import { userSelectedDeckStore } from '../lib/stores/userSelectedDeckStore';
	import { fetching } from '../lib/utils/fetching';
	import { addToastByRes } from "../lib/utils/addToToastStore";
	import type { Params } from "../lib/utils/fetching";
   
	$: if($tokenStore.length < 30) redirect("login");
	$: getAllCardsToLearn();


	let buttons = [
		{ tag: "button", id: "", text: "DeckView", action: () => redirect("") },
		{ tag: "button", id: "", text: "Logout", action: handleLogout }
	];

	let cards = [];

	async function getAllCardsToLearn(){
		let res = await fetching("/api/get-all-cards-to-learn", "GET", [{name:"deckId", value: $userSelectedDeckStore.deckId}]);
		cards = res.items;	
	}


	async function nextCard(card, g){
		let data: Params[] = [
			{name: "cardId", value: card.cardId},
			{name: "g", value: g},
		]

		let res = await fetching("/api/learn", "POST", data);
		addToastByRes(res);


		cards.shift();
		cards = [...cards];

		if(cards.length == 0){
			getAllCardsToLearn();
		}

	}
	
</script>

<svelte:head>
    <title>Learn View</title>
    <link rel="icon" type="image/gif" href="https://media.giphy.com/avatars/KirstenHurley/kdEReo8fnjUs/200h.gif"/>
</svelte:head>


<Nav title="LearnView" buttons={buttons}/>
<SvelteToast/>
<main class="mt-20">
	{#if cards.length > 0}
		<div class="grid grid-row gap-6 justify-center">
			{#key cards}
				<FlipCard card={cards[0]}/>
			{/key}
			<div class="grid grid-cols-6 gap-4">
				<div class="tooltip tooltip-error" data-tip="Keine Ahnung; totales Blackout">
					<button class="btn btn-error" on:click={()=>nextCard(cards[0], 0)}>0</button>
				</div>
				<div class="tooltip tooltip-warning" data-tip="Falsche Antwort, und nach Anzeige der Rückseite wäre die Karte schwer richtig zu 
				beantworten gewesen">
					<button class="btn btn-warning" on:click={()=>nextCard(cards[0], 1)}>1</button>
				</div>
				<div class="tooltip tooltip-secondary" data-tip="Falsche Antwort, aber nach Anzeige der Rückseite wäre die Karte leicht richtig zu 
				beantworten gewesen">
					<button class="btn btn-secondary" on:click={()=>nextCard(cards[0], 2)}>2</button>
				</div>
				<div class="tooltip tooltip-primary" data-tip="Richtige Antwort nach angestrengtem Nachdenken">
					<button class="btn btn-primary" on:click={()=>nextCard(cards[0], 3)}>3</button>
				</div>
				<div class="tooltip tooltip-info" data-tip="Richtige Antwort nach einer kurzen Pause">
					<button class="btn btn-info" on:click={()=>nextCard(cards[0], 4)}>4</button>
				</div>
				<div class="tooltip tooltip-success" data-tip="Richtige Antwort ohne langes Zögern">
					<button class="btn btn-success" on:click={()=>nextCard(cards[0], 5)}>5</button>
				</div>
			</div>
		</div>
	{:else}
	<div class="grid grid-row justify-center">
		<h1 class="mx-auto text-3xl font-bold">No Cards to Learn</h1>
		<br class="mt-4"/>
		<button class="btn btn-primary" on:click={()=>redirect("")}>Go back</button>
	</div>
	{/if}
</main>

