<script lang="ts">
	import { redirect } from '../lib/utils/redirect';
    import favicon from "/favicon.png";
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
    $: document.cookie = `Token=${$token}`;

</script>

<svelte:head>
	<link rel="icon" type="image/png" href={favicon}/>
	<title>Register</title>
</svelte:head>


<main class="flex justify-center items-center mx-auto h-screen text-white">
    
    <div class="rounded-xl shadow-2xl bg-slate-900 max-w-fit p-10">
        <h1 class="underline text-2xl mx-auto flex justify-center p-2">Register</h1>
        <form method="POST" action="api/register" on:submit|preventDefault={handleSubmit}>
            <div class="flex flex-col">
                <div class="form-control">
                    <label class="input-group">
                    <span class="w-36">Username</span>
                    <input name="username" required type="text" placeholder="Max" class="input input-bordered w-full" />
                    </label>
                </div>
                <br class="pt-4"/>
                <div class="form-control">
                    <label class="input-group">
                    <span class="w-36">Email</span>
                    <input name="email" required type="email" placeholder="test@example" class="flex input input-bordered w-full" />
                    </label>
                </div>
                <br class="pt-4"/>
                <div class="form-control">
                    <label class="input-group">
                    <span class="w-36">Password</span>
                    <input name="password" required type="password" placeholder="1234" class="input input-bordered w-full" />
                    </label>
                </div>
                <div class="flex justify-center mt-2">
                    <button type="submit" class="btn btn-primary">Register</button>
                </div>
            </div>
        </form>
    </div>
</main>
