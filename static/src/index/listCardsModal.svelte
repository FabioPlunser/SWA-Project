<script lang="ts">
    import Modal from "$components/Modal.svelte";
    import DualSideCard from "$components/DualSideCard.svelte";
    import Spinner from "$components/Spinner.svelte";
    import { fetching } from "$utils/fetching";

    export let listCards = false;
    export let selectedDeck = null; 

    async function getCardsFromDeck(deck){
        let res = await fetching("/api/get-cards-of-deck", "GET", [{name: "deckId", value: deck.deckId}]);
        return res.items;
    }
</script>


<Modal open={listCards} on:close={()=>listCards=false} closeOnBodyClick={true}>
    <div class="relative max-w-full">
        <div class="flex flex-col min-w-fit">
            <h1 class="flex justify-center text-2xl font-bold">Cards of Deck {selectedDeck.name}</h1>
            <div class="absolute right-0">
                <button class="btn btn-primary" on:click={()=> listCards = false}>Close</button>
            </div>
            <div class="grid grid-cols-4 gap-2 mt-6">
                {#await getCardsFromDeck(selectedDeck)}
                    <Spinner/>
                {:then cards}
                    {#each cards as card, index (card.cardId)}
                        <div>
                            <DualSideCard {card} {index} editable={false} cardBg="bg-slate-800" textBg="bg-slate-700"/>
                        </div>
                    {/each}
                {/await}
            </div>
        </div>
    </div>
</Modal>