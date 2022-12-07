<script lang="ts">
    import FlipCard from "../lib/components/flipCard.svelte";
    import Slider from "../lib/components/slider.svelte";
	import Nav from "../lib/components/nav.svelte";
	import { redirect } from '../lib/utils/redirect';
    import { token } from "../lib/stores/token";
    $: if($token.length < 30) redirect("login");
</script>

<svelte:head>
    <title>Learn View</title>
    <link rel="icon" type="image/gif" href="https://media.giphy.com/avatars/KirstenHurley/kdEReo8fnjUs/200h.gif"/>
</svelte:head>


<Nav title="Learnview"/>
<main class="mt-20">
	<div class="grid grid-row gap-6 justify-center">
		<FlipCard />
		<div class="flex">
			<Slider/>
			<button class="btn btn-primary">Next</button>
		</div>
	</div>
</main>

