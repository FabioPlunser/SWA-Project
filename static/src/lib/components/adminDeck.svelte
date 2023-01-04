<script lang="ts">
    import { createEventDispatcher } from 'svelte';
    const dispatch = createEventDispatcher();

    import { tokenStore } from "../stores/tokenStore";
    import { addToast, addToastByRes } from '../utils/addToToastStore';

    export let deck; 
    let { deckId, name, description, published, blocked} = deck;
    
    
    let hover = false
    function handleMouseOut() {
        hover = false
    }
    function handleMouseOver() {
        hover = true
    }
    

    const myHeaders = new Headers();
    myHeaders.append("Authorization", "Bearer " + $tokenStore);
    
    async function handleBlockDeck(){
        blocked = !blocked;
        if(blocked){
            let res = await fetch(`/api/block-deck?deckId=${deckId}`, {
                method: "PUT",
                headers: myHeaders,            
            });
            res = await res.json();
            addToastByRes(res);
            dispatch('blockDeck', "blockDeck");
        }
        if(!blocked){
            let res = await fetch(`/api/unblock-deck?deckId=${deckId}`, {
                method: "PUT",
                headers: myHeaders,            
            });
            res = await res.json();
            addToastByRes(res);
            dispatch('blockDeck', "blockDeck");
        }
    }
    
    

    async function handleDeleteDeck() {
        let res = await fetch(`/api/delete-deck?deckId=${deckId}`, {
            method: "DELETE",
            headers: myHeaders,
        });
        res = await res.json();
        addToastByRes(res);
        dispatch('deleteDeck', "deleteDeck");
    }

    
</script>


<!-- svelte-ignore a11y-mouse-events-have-key-events -->
{#if !blocked}
<div class="bg-slate-900 rounded-xl shadow-xl p-5 h-96 relative" on:mouseover={handleMouseOver} on:mouseout={handleMouseOut}>
        <div class="{hover ? "hidden" : "block"}" >
            <h1 class="underline flex justify-center text-xl">{name}</h1>
            <br class="my-4"/>
            <p>{description}</p>
            <br class="my-4"/>
            <div class="bottom-0 absolute mb-4">
                <div class="grid grid-rows-3 gap-2">
                    <div class="badge badge-primary">Cards to learn: </div>
                    {#if published}
                    <div class="badge badge-info">Published</div>
                    {:else}
                    <div class="badge badge-error">Not Published</div>
                    {/if}
                    Progress: <progress class="progress progress-success bg-gray-700" value={50} max={100}></progress>
                </div>
            </div>
        </div>

        <div class="{hover ? "block" : "hidden"} grid grid-row gap-2">
                <button class="btn btn-primary" on:click={handleBlockDeck}>Block Deck</button>
                <button class="btn btn-primary" on:click={handleDeleteDeck}>Delete Deck</button>
        </div>
</div>
{/if}

{#if blocked}
<!-- svelte-ignore a11y-mouse-events-have-key-events -->
<div class="bg-slate-900 rounded-xl shadow-xl p-5 h-96 relative opacity-50" on:mouseover={handleMouseOver} on:mouseout={handleMouseOut}>
    <div class="{hover ? "hidden" : "block"}" >
        <h1 class="underline flex justify-center text-xl">{name}</h1>
        <br class="my-4"/>
        <p>{description}</p>
    </div>

    <div class="{hover ? "block" : "hidden"} grid grid-row gap-2">
        <button class="btn btn-primary" on:click={handleBlockDeck}>Unblock Deck</button>
    </div>
</div>
{/if}