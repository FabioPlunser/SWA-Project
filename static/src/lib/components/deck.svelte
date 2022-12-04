<script lang="ts">
    
    import { onMount } from 'svelte';
    import { redirect } from '../utils/redirect';
    import { loggedIN } from '../stores/loggedIn';
    export let title = "title";
    export let description = "description";
    // export let id: number = 0;
    export let learned = 0; 
    export let NotLearned = 0;

    let hover = false
    function handleMouseOut() {
        hover = false
    }
    function handleMouseOver() {
        hover = true
    }
    onMount(() => {
        if(!$loggedIN){
            redirect("login");
        }
    });
</script>


<!-- svelte-ignore a11y-mouse-events-have-key-events -->
<div class="bg-slate-900 rounded-xl shadow-xl p-10 h-96 " on:mouseover={handleMouseOver} on:mouseout={handleMouseOut}>
        <div class="{hover ? "hidden" : "block"}" >
            <h1 class="underline flex justify-center">{title}</h1>
            <br class="my-4"/>
            <p>{description}</p>
            <br class="my-4"/>
            <div class="flex justify-between">
                <div>
                    <p>Learned</p>
                    <p class="flex justify-center">{learned}</p>
                </div>
                <div>
                    <p>Not learned</p>
                    <p class="top-0 flex justify-center">{NotLearned}</p>
                </div>
            </div>
        </div>


        <div class="{hover ? "block" : "hidden"} grid grid-row gap-2">
                <button class="btn btn-primary">Learn Deck</button>
                <button class="btn btn-primary">Show Deck</button>
                <button class="btn btn-primary">Edit Deck</button>
                <button class="btn btn-primary">Publish Deck</button>
                <button class="btn btn-primary">Delete Deck</button>
        </div>
</div>