<script>
	import { onMount } from 'svelte';
    import Admin from './Admin.svelte';
    let isAdmin = false;

    onMount(async () =>{
        let res = await fetch("api/isAdmin")
        res = await res.json()
        isAdmin = res.isAdmin
    })
</script>

{#if isAdmin}
    <Admin/>
{:else}
    <h1>>ou have no Admin rights</h1>
{/if}