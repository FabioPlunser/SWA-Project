<script lang="ts">
  export let card; 
  let {frontText, backText, isFlipped} = card;
	import { fly, fade } from 'svelte/transition';

  function handleFlip(){
    isFlipped = !isFlipped;
  }
</script>

<!-- svelte-ignore a11y-click-events-have-key-events -->
{#key card}
  <div id="flip-box" class="[perspective:1000px] bg-transparent w-96 h-80 transition-all text-2xl" on:click={handleFlip}>
    <div id="flip-box-inner" class="relative w-full h-full duration-100 [transform-style:preserve-3d]" class:flip-it={isFlipped}>
      <div id="flip-box-front" class="flex justify-center absolute w-full h-full [-webkit-backface-visibility: hidden] [backface-visibility:hidden] bg-slate-900 text-white shadow-xl rounded-2xl"> 
        <div class="flex items-center m-2 justify-center">
            <h1>{frontText}</h1>

        </div>
      </div>
      <div id="flip-box-back" class="flex justify-center absolute w-full h-full [backface-visibility:hidden] bg-slate-900 text-white shadow-xl rounded-2xl [transform:rotateY(180deg)] [transform: translate (-50%, -50%)]" class:conceal-answer={isFlipped}>
        <div class="flex items-center m-2">
            <span>{backText}</span>
        </div>
      </div>  
    </div>
  </div>
{/key}
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