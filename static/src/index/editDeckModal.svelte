<script lang="ts">
    import Modal from "$components/Modal.svelte";
    import Form from "$components/Form.svelte";
    import SvelteMarkdown from "svelte-markdown";
    import { Validators } from "$utils/Validators";
    import type { ValidatorFn} from "$utils/Validators";
    import { formFormat } from "$types/formFormat";
    import { redirect } from "$utils/redirect";
    import { userSelectedDeckStore } from "$stores/userSelectedDeckStore";
    import { fetching } from "$lib/utils/fetching";
    import autosize from 'svelte-autosize';
  import { addToast } from "$lib/utils/addToToastStore";
    export let showEditDeckModal = false; 
    export let getDecks = null; 
    export let selectedDeck = null;  
    let descriptionFocus = false;

    let formValidators: {
        [inputName: string]: {
        validators: ValidatorFn[];
        };
    } = {
        name: {
        validators: [Validators.required],
        },
        description: {
        validators: [Validators.required],
        },
        isPublished: {
        validators: [Validators.required],
        },
    };

    let cards = [];
    $: getCards();
    async function getCards(){
        let res = await fetching("/api/get-cards-of-deck", "GET", [{name: "deckId", value: selectedDeck.deckId}]);
        cards = res.items; 
    }

    function handlePostFetch(data){
        if(!data.detail.res.success){
            addToast("Couldn't update Deck");
        }
        getDecks();
    }
   
</script>

<Modal open={showEditDeckModal} on:close={()=> showEditDeckModal = false} closeOnBodyClick={false}>
    <div class="max-w-max">
        <h1 class="flex justify-center text-2xl font-bold">Edit Deck</h1>
        <br class="pt-4"/>
        <Form url="/api/update-deck" method="POST" {formValidators} dataFormat={formFormat.JSON} addJSONData={[{cards: cards}]} on:postFetch={handlePostFetch}>
            <input name="deckId" bind:value={selectedDeck.deckId} type="hidden" required>
            <div class="flex flex-col">
                <div class="form-control">
                    <label class="input-group">
                    <span class="w-36">Name</span>
                    <input bind:value={selectedDeck.name} name="name" required type="text" placeholder="Softwarearchitecture" class="input input-bordered w-full" />
                    </label>
                </div>
                <br class="pt-4"/>
                <div class="form-control">
                    <label class="input-group">
                    <span class="w-36">Description</span>
                    <input type="hidden" name="description" bind:value={selectedDeck.description}>
                    {#if descriptionFocus}
                        <textarea use:autosize on:mouseleave={()=>descriptionFocus=false} name="description" contenteditable id="divTextarea" bind:value={selectedDeck.description} placeholder="Description" class="bg-slate-800  min-h-full max-w-full  w-full p-2 rounded-xl resize"/>
                    {:else}
                        <!-- svelte-ignore a11y-click-events-have-key-events -->
                        <div on:click={()=>descriptionFocus=true} id="divTextarea" placeholder="Description" class="bg-slate-800 min-h-full w-full p-2 rounded-xl prose prose-dark prose-sm"><SvelteMarkdown source={selectedDeck.description}/></div>  
                    {/if}
                    </label>
                </div>
                <br class="pt-2"/>
                <button class="btn btn-primary" type="button" on:click={()=>{$userSelectedDeckStore = selectedDeck; redirect("edit-cards")}}>Edit Cards</button>
                <br class="pt-4"/>
                <div class="flex justify-between">
                    <button type="submit" class="btn btn-primary" on:click={()=>showEditDeckModal=false}>Update</button>
                    <input type="reset" class="btn btn-primary" value="Clear"/>
                    <!-- svelte-ignore a11y-click-events-have-key-events -->
                    <button type="button" class="btn btn-primary" on:click={()=> showEditDeckModal = false}>Close</button>
                </div>
            </div>
        </Form>
    </div>
</Modal>