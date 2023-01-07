<script lang="ts">
    import favicon  from '../assets/favicon.png';
    import SvelteToast from "../lib/components/SvelteToast.svelte";
    import Form from "../lib/components/Form.svelte";

	import { redirect } from '../lib/utils/redirect';
    import { tokenStore } from "../lib/stores/tokenStore";
    import { personIdStore } from "../lib/stores/personIdStore";
    import { userPermissionsStore } from "../lib/stores/userPermissionsStore";
    import { Validators} from "../lib/utils/Validators";

    $: if($tokenStore.length > 30) redirect("");
    $: document.cookie = `Token=${$tokenStore}`;
    
    let errors = {};
    let formValidators = {
        username: {
            validators: [Validators.required],
        },
        password: {
            validators: [Validators.required, Validators.minLength(8)],
        },
        email: {
            validators: [Validators.required, Validators.email],
        },
    };

    function handlePostFetch(data){
        let res = data.detail.res;
        $tokenStore = res.token;
        $personIdStore = res.id;
        $userPermissionsStore= res.permissions;
    }
</script>

<svelte:head>
	<link rel="icon" type="image/png" href={favicon}/>
	<title>Register</title>
</svelte:head>

<SvelteToast/>
<main class="flex justify-center items-center mx-auto h-screen text-white">
    <div class="rounded-xl shadow-2xl bg-slate-900 max-w-fit p-10">
        <h1 class="underline text-2xl mx-auto flex justify-center p-2">Register</h1>
        <Form url="/api/register" method="POST" dataFormat="FormData" {formValidators} bind:errors on:postFetch={handlePostFetch}>
            <div class="flex flex-col">
                <div class="form-control">
                    <label class="input-group">
                    <span class="w-36">Username</span>
                    <input name="username" type="text" placeholder="Max" class="input input-bordered w-full" />
                    </label>
                    {#if errors?.username?.required?.error}
                        <p class="text-red-500">Username is required</p>
                    {/if}
                </div>
                <br class="pt-4"/>
                <div class="form-control">
                    <label class="input-group">
                    <span class="w-36">Email</span>
                    <input name="email" type="email" placeholder="test@example" class="flex input input-bordered w-full" />
                    </label>
                    {#if errors?.email?.required?.error}
                        <p class="text-red-500">Email is required</p>
                    {/if}
                    {#if errors?.email?.email?.error}
                        <p class="text-red-500">{errors.email.email.message}</p>
                    {/if}
                </div>
                <br class="pt-4"/>
                <div class="form-control">
                    <label class="input-group">
                    <span class="w-36">Password</span>
                    <input name="password" type="password" placeholder="1234" class="input input-bordered w-full" />
                    </label>
                    {#if errors?.password?.required?.error}
                        <p class="text-red-500">Password is required</p>
                    {/if}
                    {#if errors?.password?.minLength?.error}
                        <p class="text-red-500">{errors.password.minLength.message}</p>
                    {/if}
                </div>
                <div class="flex justify-center mt-2">
                    <button type="submit" class="btn btn-primary">Register</button>
                </div>
            </div>
        </Form>
    </div>
</main>
