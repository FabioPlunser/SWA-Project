<script>
  export let title = "Hello World";
  export let answer = "Was geht";
  export let editable = false;
  let flipped = false;
</script>

<!-- svelte-ignore a11y-click-events-have-key-events -->
<div id="flip-box" class="[perspective:1000px] bg-transparent w-96 h-80 transition-all" on:click={()=> flipped = !flipped}>
  <div id="flip-box-inner" class="relative w-full h-full duration-100 [transform-style:preserve-3d]" class:flip-it={flipped}>
    <div id="flip-box-front" class="flex justify-center absolute w-full h-full [-webkit-backface-visibility: hidden] [backface-visibility:hidden] bg-slate-800 text-white shadow-xl rounded-2xl p-4 m-4"> 
      <div>
        {#if editable}
          <textarea bind:value={title} placeholder={title} class="input"/>
        {:else}
          <h1>{title}</h1>
        {/if}
      </div>
    </div>
    <div id="flip-box-back" class="flex justify-center absolute w-full h-full [backface-visibility:hidden] bg-slate-800 text-white shadow-xl rounded-2xl p-4 m-4 [transform:rotateY(180deg)] [transform: translate (-50%, -50%)]" class:conceal-answer={flipped}>
      <div>
        {#if editable}
          <textarea bind:value={answer} placeholder={answer} class="input"/>
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