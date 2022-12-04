<script lang="ts">
    export let value = 0;
</script>

<div class="w-full">
    <input type="range" min="0" max="5" bind:value={value} class="range" step="1" />
    <div class="w-full flex justify-between text-xs px-2">
    <span>0</span>
    <span>1</span>
    <span>2</span>
    <span>3</span>
    <span>4</span>
    </div>
</div>
