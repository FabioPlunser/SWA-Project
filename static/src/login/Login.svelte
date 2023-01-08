<script lang="ts">
    import favicon  from '../assets/favicon.png';
    import SvelteToast from "../lib/components/SvelteToast.svelte";
    import Form from "../lib/components/Form.svelte";


    import { redirect } from "../lib/utils/redirect";
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
            validators: [Validators.required],
        },
    };

    function handlePostFetch(data){
        let res = data.detail.res; 
        $tokenStore = res.token;
        $personIdStore = res.personId;
        $userPermissionsStore= res.permissions;
    }
</script>

<svelte:head>
	<link rel="icon" type="image/png" href="{favicon}"/>
	<title>Login</title>
</svelte:head>

<SvelteToast/>
<main class="flex justify-center items-center mx-auto h-screen text-white">
    <div class="rounded-xl shadow-2xl bg-slate-900 max-w-fit p-10">
        <h1 class="font-bold text-2xl mx-auto flex justify-center p-2">Login</h1>
        <Form url="/api/login" method="POST" dataFormat="FormData" {formValidators} bind:errors on:postFetch={handlePostFetch}>
            <div class="form-control">
                <label class="input-group">
                  <span>Username</span>
                  <input name="username" type="text" placeholder="Max" class="input input-bordered" />
                </label>
                {#if errors?.username?.required?.error}
                    <p class="text-red-500">Username is required</p>
                {/if}
                {#if errors?.username?.minLength?.error}
                <p class="text-red-500">{errors.username.minLength.message}</p>
            {/if}
            </div>
            <br class="pt-4"/>
            <div class="form-control">
                <label class="input-group">
                  <span>Password</span>
                  <input name="password" type="password" placeholder="1234" class="input input-bordered" />
                </label>
                {#if errors?.password?.required?.error}
                    <p class="text-red-500">Password is required</p>
                {/if}
                {#if errors?.password?.minLength?.error}
                    <p class="text-red-500">{errors.password.minLength.message}</p>
                {/if}
            </div>
            <div class="flex justify-between pt-4">
                <button type="submit" class="btn btn-primary">Login</button>
                <button type="button" class="btn btn-primary" on:click={()=>redirect("register")}>Create Account</button>
            </div>
        </Form>
    </div>
</main>



