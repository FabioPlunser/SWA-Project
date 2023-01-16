<script lang="ts">
    import favicon  from '$assets/favicon.png';
    import SvelteToast from "$components/SvelteToast.svelte";
    import Form from "$components/Form.svelte";
    import FormError from '$components/formError.svelte';

    import { redirect } from "$utils/redirect";
    import { jwt } from "$stores/jwtStore";
    import { personIdStore } from "$stores/personIdStore";
    import { userPermissionsStore } from "$stores/userPermissionsStore";
    import { Validators} from "$utils/Validators";
    import { addToast, addToastByRes } from '$utils/addToToastStore';
  import { formFormat } from '$lib/types/formFormat';

    $: if($jwt && !$jwt.expired) redirect("");
    $: {
        document.cookie = `token=${$jwt?.token}`;
        document.cookie = `username=${$jwt?.username}`;
    }

    let username = "";

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
        
        if(res.success){
            $jwt = {token: res.token, username: username}
            $personIdStore = res.personId;
            $userPermissionsStore= res.permissions;
        }else{
            addToastByRes(res);
        }        
    }
</script>

<svelte:head>
	<link rel="icon" type="image/png" href="{favicon}"/>
	<title>Login</title>
    <script src="http://localhost:35729/livereload.js"></script>
</svelte:head>

<SvelteToast/>
<main class="flex justify-center items-center h-screen text-white">
    
    <div class="rounded-xl shadow-2xl bg-slate-900 max-w-fit p-10">
        {#if $jwt && $jwt.expired}
            <p class="text-red-500 flex justify-center">Your session expired please login again</p>
        {/if}
        <div class="flex justify-center">
            <img class="flex justify-center w-24" src={favicon} alt="favicon"/>
            <h1 class="items-center text-2xl font-bold flex justify-center">Memory</h1>
        </div>
        <h1 class="font-bold text-2xl flex justify-center p-2">Login</h1>
        <Form url="/api/login" method="POST" dataFormat={formFormat.FORM} {formValidators} on:postFetch={handlePostFetch}>
            <div class="form-control">
                <label class="input-group">
                  <span>Username</span>
                  <input name="username" bind:value={username} type="text" placeholder="Max" class="input input-bordered" />
                </label>
                <FormError name="username" key="required" message="Username is required"/>
            </div>
            <br class="pt-4"/>
            <div class="form-control">
                <label class="input-group">
                  <span>Password</span>
                  <input name="password" type="password" placeholder="1234" class="input input-bordered" />
                </label>
                <FormError name="password" key="required" message="Password is required"/>
                <FormError name="password" key="minLength" message="Password must be at least 8 characters"/>
            </div>
            <div class="flex justify-between pt-4">
                <button type="submit" class="btn btn-primary">Login</button>
                <button type="button" class="btn btn-primary" on:click={()=>redirect("register")}>Create Account</button>
            </div>
        </Form>
    </div>
</main>



