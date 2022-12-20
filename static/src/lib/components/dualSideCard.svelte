<script lang="ts">
    export let card;
    let {id, question, answer} = card;
</script>


<div class="card bg-gray-900 p-8 w-auto">
    <div class="flex flex-row w-auto p-2">
        <div class="grid flex-grow h-32 card bg-base-300 rounded-box justify-center w-auto">{question}</div> 
        <div class="divider divider-horizontal"></div> 
        <div class="grid flex-grow h-32 card bg-base-300 rounded-box justify-center w-atuo">{answer}</div>
      </div>
</div>