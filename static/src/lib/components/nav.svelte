<script lang="ts">
    export let title = "";
    import favicon from "/favicon.png";
    import { redirect } from "../utils/redirect";
    import { token } from "../stores/token";
    function handleLogout(){
        $token = null;
        redirect("login");
    }
</script>

<div class="flex p-2 justify-center bg-slate-900 w-full rounded-none justiy-center shadow-2xl top-0 fixed z-10">
    <div class="flex-1 flex">
      <a href="/" class=""><img src={favicon} alt="logo" class="w-12 rounded-full shadow-xl"/></a>
      <div class="flex justify-center mx-auto items-center">
          <h1 class="text-2xl underline">{title}</h1>
      </div>
     
    </div>
    <div class="text-xl">
        
        <ul class="menu menu-horizontal p-0 mx-auto justify-center">
            
        <li>
          <button class="rounded-xl" on:click={handleLogout}>
            Logout
          </button>
      </ul>
    </div>
  </div>