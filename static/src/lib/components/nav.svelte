<script lang="ts">
  import favicon  from '../../assets/favicon.png';
  export let title = "";
  export let buttons = [];
</script>

<div class="flex p-2 justify-center bg-slate-900 w-full rounded-none justiy-center shadow-2xl top-0 fixed z-10">
    <div class="flex-1 flex">
      <a href="/" class=""><img src={favicon} alt="logo" class="w-12 rounded-full shadow-xl"/></a>
      <h1 class="flex ml-2 text-3xl font-bold items-center">{title}</h1>
    </div>

    <div class="menu menu-horizontal">
      {#each buttons as button}
        <button class="flex justify-center btn btn-primary mx-2" on:click={button.action}>{@html button.text}</button>
      {/each}
    </div>
</div>