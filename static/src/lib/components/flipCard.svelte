<script>
  export let title = "SOLID";
  export let answer = "Singleresponsibility, Open-closed, Liskov substitution, Interface segregation, Dependency inversion";
  export let editable = false;
  let flipped = false;

  function handleFlip(){
    if(editable) return;
    flipped = !flipped;
  }

 
</script>

<!-- svelte-ignore a11y-click-events-have-key-events -->
<div id="flip-box" class="[perspective:1000px] bg-transparent w-96 h-80 transition-all text-2xl" on:click={handleFlip}>
  <div id="flip-box-inner" class="relative w-full h-full duration-100 [transform-style:preserve-3d]" class:flip-it={flipped}>
    <div id="flip-box-front" class="flex justify-center absolute w-full h-full [-webkit-backface-visibility: hidden] [backface-visibility:hidden] bg-slate-900 text-white shadow-xl rounded-2xl"> 
      <div class="flex items-center m-2 justify-center">
        {#if editable}
          <div class="w-full h-full">
            <textarea  bind:value={title} placeholder={title} class="input"/>
            <button class="btn btn-primary bottom-0 absolute left-0 right-0" on:click={()=> flipped = !flipped}>Flip</button>
          </div>
        {:else}
          <h1>{title}</h1>
        {/if}

      </div>
    </div>
    <div id="flip-box-back" class="flex justify-center absolute w-full h-full [backface-visibility:hidden] bg-slate-900 text-white shadow-xl rounded-2xl [transform:rotateY(180deg)] [transform: translate (-50%, -50%)]" class:conceal-answer={flipped}>
      <div class="flex items-center m-2">
        {#if editable}
          <div class="w-full h-full">
            <textarea  bind:value={answer} placeholder={title} class="input"/>
            <button class="btn btn-primary bottom-0 absolute left-0 right-0" on:click={()=> flipped = !flipped}>Flip</button>
          </div>
        {:else}
          <span>{answer}</span>
        {/if}
      </div>
    </div>  
  </div>
</div>

<style>
  @keyframes revealTextSlowly {
		to { color: white }
	}
	
	.conceal-answer {
		animation: revealTextSlowly .3s forwards;
	}
  .flip-it{
    transform: rotateY(180deg);
    /* transform: translate (-50%, -50%) */
  }
</style>