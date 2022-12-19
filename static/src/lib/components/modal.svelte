
<script lang="ts">
    import { createEventDispatcher, onDestroy } from 'svelte';
    const dispatch = createEventDispatcher();
	const close = () => dispatch('close');
    /**
     * Id for modal must be unique
     */
    export let uniqueModalQualifier = "title";
    export let disableModalClick = false;
    export let width = "w-96";


    function handleDispatch() {
        // console.log("Send data of " + uniqueModalQualifier + " to server");
    }
    const previously_focused = typeof document !== 'undefined' && document.activeElement;
	if (previously_focused) {
		onDestroy(() => {
			previously_focused.focus();
            handleDispatch();
		});
	}
    function handleKeyDown(event) {
        console.log(event.key);
        if (event.key === 'Escape') {
            close();
        }
    }
</script>


<input type="checkbox" id="{uniqueModalQualifier}" class="modal-toggle" on:keydown={handleKeyDown}/>
	<!-- svelte-ignore a11y-click-events-have-key-events -->
    {#if disableModalClick}
        <label for="{uniqueModalQualifier}" id="modal-background" class="modal cursor-pointe">
            <label class="modal-box relative overflow-auto bg-slate-900 w-fit h-fit" id="modal">
                <slot/>
            </label>
        </label>
    {:else}
        <label for="{uniqueModalQualifier}" id="modal-background" class="modal cursor-pointe" on:click={close}>
            <label class="modal-box relative overflow-auto bg-slate-900" id="modal">
                <slot/>
            </label>
        </label>
    {/if}

