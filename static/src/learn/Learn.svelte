<script lang="ts">
    import FlipCard from "$components/flipCard.svelte";
	import Nav from "$components/nav.svelte";
	import SvelteToast from "$components/SvelteToast.svelte";
	
	import { redirect } from '$utils/redirect';
	import { userSelectedDeckStore } from '$stores/userSelectedDeckStore';
	import { fetching } from '$utils/fetching';
	import { addToastByRes } from "$utils/addToToastStore";
	import type { Params } from "$utils/fetching";
	import { fly, fade } from 'svelte/transition';

   
	$: getAllCardsToLearn();

	let buttons = [
		{ text: "Home", href: "/" },
	];

	$: console.log(cards);
	let cards = [];

	async function getAllCardsToLearn(){
		let res = await fetching("/api/get-all-cards-to-learn", "GET", [{name:"deckId", value: $userSelectedDeckStore.deckId}]);
		cards = res.items;	
		checkIsFlipped();
	}

	function checkIsFlipped()
	{
		let flippableCards = Array.of(cards.find(c => c.isFlipped));
		console.log("flippedCards", flippableCards);
		if(flippableCards){
			for(let card of flippableCards){
				cards.push({cardId: card.cardId, frontText: card.frontText, backText: card.backText, isFlipped: false});
			}
		}	
	}

	async function nextCard(card, g){
		let data: Params[] = [
			{name: "cardId", value: card.cardId},
			{name: "g", value: g},
		]

		let res = await fetching("/api/learn", "POST", data);
		// addToastByRes(res);


		cards.shift();

		if(cards.length == 0){
			await getAllCardsToLearn();	
		}

		cards = [...cards];
	}
	
</script>

<svelte:head>
    <title>Learn View</title>
    <link rel="icon" type="image/gif" href="https://media.giphy.com/avatars/KirstenHurley/kdEReo8fnjUs/200h.gif"/>
    <script src="http://localhost:35729/livereload.js"></script>
</svelte:head>


<Nav title="LearnView" buttons={buttons}/>
<!-- <SvelteToast/> -->
<main class="mt-20">
	{#if cards.length > 0}
		<div class="flex justify-center">
			{#key cards}
				<div in:fly={{y: -100, duration: 300}} out:fly={{y: 100, duration: 300}}>
					<FlipCard card={cards[0]}/>
				</div>
			{/key}
		</div>

		<br class="mt-10"/>

		<div class="grid grid-row gap-6 justify-center">
			<div class="grid grid-cols-6 gap-4 justify-center">
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

