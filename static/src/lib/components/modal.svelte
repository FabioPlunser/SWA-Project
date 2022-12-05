
<script lang="ts">
    import { createEventDispatcher, onDestroy } from 'svelte';
    const dispatch = createEventDispatcher();
	const close = () => dispatch('close');
    /**
     * Id for modal must be unique
     */
   export let uniqueModalId = "title";

    let modal;

    function handleDispatch() {
        console.log("Send data of " + uniqueModalId + " to server");
    }
    const previously_focused = typeof document !== 'undefined' && document.activeElement;
	if (previously_focused) {
		onDestroy(() => {
			previously_focused.focus();
            handleDispatch();
		});
	}
</script>


<input type="checkbox" id="{uniqueModalId}" class="modal-toggle"/>
	<label for="{uniqueModalId}" id="modal-background" class="modal cursor-pointe" on:click={close}>
		<label class="modal-box relative overflow-auto" id="modal" bind:this={modal}>
            <slot/>
            <div class="modal-action">
                <label for="{uniqueModalId}" class="btn btn-primary" autofocus on:click={close}>Close</label>
            </div>
    </label>
</label>
