<script lang="ts">
	import Nav from './lib/components/nav.svelte';
	import Deck from './lib/components/deck.svelte';
	import { redirect } from "./lib/utils/redirect";
	import { token } from "./lib/stores/token";

	$: if($token.length < 30) redirect("login");
</script>

<!-- This site is index -> deck overview -->
<svelte:head>
	<link rel="icon" type="image/png" href="/favicon.png"/>
	<title>Decks overview</title>
</svelte:head>

<Nav title="Decks overview"/>
<main class="m-20">
	<div class="grid grid-cols-4 gap-4">
		{#each  [...Array(20).keys()] as i}
			<Deck title="Deck {i}" description="Description" learned={i} NotLearned={i*3}/>
		{/each}
	</div>
</main>
