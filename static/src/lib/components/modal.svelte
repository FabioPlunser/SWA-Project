
<script lang="ts">
	import { scale } from 'svelte/transition';
    import { createEventDispatcher, onDestroy } from 'svelte';
    const dispatch = createEventDispatcher();
	const close = () => dispatch('close');
    /**
     * Id for modal must be unique
     */
    export let open = false;
    export let closeOnBodyClick = true;


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

<svelte:window on:keydown={handleKeyDown}/>

{#if closeOnBodyClick}
<div class="modal cursor-pointer" class:modal-open={open} on:keydown={handleKeyDown} on:click={close} >
    <div transition:scale={{duration:150}} class="modal-box relative overflow-auto bg-slate-900">
            <slot/>
    </div>
</div>
{:else}
<div class="modal cursor-pointer" class:modal-open={open} on:keydown={handleKeyDown} on:click|self={close} >
    <div transition:scale={{duration:150}} class="modal-box relative overflow-auto bg-slate-900">
            <slot/>
    </div>
</div>
{/if}