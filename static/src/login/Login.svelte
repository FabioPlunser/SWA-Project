<script lang="ts">
    import favicon from "/favicon.png";
    import { redirect } from "../lib/utils/redirect";
    import { formFetch } from "../lib/utils/formFetch";
    import { token } from "../lib/stores/token";

    // TODO add validation
    async function handleSubmit (e){
        let res = await formFetch(e);
        if(!res.success){
            alert(res.message);
            return;
        }
        $token = res.token;
	}
    $: if($token.length > 30) redirect("");
    $: document.cookie = `Token=${$token}; path=/;`;
</script>

<svelte:head>
	<link rel="icon" type="image/png" href={favicon}/>
	<title>Login</title>
</svelte:head>


<main class="flex justify-center items-center mx-auto h-screen text-white">
    <div class="rounded-xl shadow-2xl bg-slate-900 max-w-fit p-10">
        <h1 class="underline text-2xl mx-auto flex justify-center p-2">Login</h1>
        <form method="POST" action="/api/login" on:submit|preventDefault={handleSubmit}>
            <div class="form-control">
                <label class="input-group">
                  <span>Username</span>
                  <input name="username" required type="text" placeholder="Max" class="input input-bordered" />
                </label>
            </div>
            <br class="pt-4"/>
            <div class="form-control">
                <label class="input-group">
                  <span>Password</span>
                  <input name="password" required type="password" placeholder="1234" class="input input-bordered" />
                </label>
            </div>
            <div class="flex justify-between pt-4">
                <button type="submit" class="btn btn-primary">Login</button>
                <button class="btn btn-primary" on:click={()=>redirect("register")}>Register</button>
            </div>
        </form>
    </div>
</main>



