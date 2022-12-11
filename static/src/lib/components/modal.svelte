
<script lang="ts">
    import { createEventDispatcher, onDestroy } from 'svelte';
    const dispatch = createEventDispatcher();
	const close = () => dispatch('close');
    /**
     * Id for modal must be unique
     */
   export let uniqueModalQualifier = "title";

    let modal;

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
        if (event.key === 'Escape') {
            close();
        }
    }
</script>


<input type="checkbox" id="{uniqueModalQualifier}" class="modal-toggle" on:keydown={handleKeyDown}/>
	<label for="{uniqueModalQualifier}" id="modal-background" class="modal cursor-pointe" on:click={close}>
		<label class="modal-box relative overflow-auto bg-slate-900" id="modal" bind:this={modal}>
            <slot/>
            <!-- <div class="modal-action"> -->
        <!-- <label for="{uniqueModalQualifier}" class="btn btn-primary" autofocus on:click={close}>Close</label> -->
            <!-- </div> -->
    </label>
</label>
