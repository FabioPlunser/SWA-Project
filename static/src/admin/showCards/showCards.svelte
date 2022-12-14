<script lang="ts">
  import favicon from "/favicon.png";
  import Nav from "../../lib/components/nav.svelte";
  import { redirect } from '../../lib/utils/redirect';
  import { handleLogout } from "../../lib/utils/handleLogout";
  let buttons = [
    { tag: "button", id: "", text: "Home", action: () => redirect("") },
    { tag: "button", id: "", text: "Admin", action: () => redirect("admin") },
    { tag: "button", id: "", text: "Logout",action: () => handleLogout()}
  ];
</script>

<svelte:head>
  <title>Admin ShowCards</title>
  <link rel="icon" type="image/png" href="{favicon}" />
</svelte:head>

<Nav title="ShowCards" {buttons}/>
<main class="mt-20 m-2">
  <h1>ShowCards</h1>
</main>