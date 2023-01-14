<script lang="ts">
    import Modal from "$components/Modal.svelte";
    import Form from "$components/Form.svelte";
    import { redirect } from "$utils/redirect";
    import { userSelectedDeckStore } from "$stores/userSelectedDeckStore";
    export let showEditDeckModal = false; 
    export let getDecks = null; 
    export let selectedDeck = null;    
</script>

<Modal open={showEditDeckModal} on:close={()=> showEditDeckModal = false} closeOnBodyClick={false}>
    <div class="max-w-full">
        <h1 class="flex justify-center text-2xl font-bold">Edit Deck</h1>
        <br class="pt-4"/>
        <Form url="/api/update-deck" method="POST" dataFormat="JSON" on:postFetch={getDecks}>
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
                    <textarea bind:value={selectedDeck.description} name="description" required placeholder="A deck to learn softwarearchitecture" class="textarea input-bordered w-full" />
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