<script lang="ts">
    import Nav from "../lib/components/nav.svelte";
    import { redirect } from '../lib/utils/redirect';
    import { token } from "../lib/stores/token";
    $: if($token.length < 30) redirect("login");

</script>


<svelte:head>
    <link rel="icon" type="image/png" href="/favicon.png"/>
    <title>ListCards</title>
</svelte:head>
<Nav title="Listcards"/>
<main class="mt-20">

</main>