<script lang="ts">
    import favicon  from '$assets/favicon.png';
    import SvelteToast from "$components/SvelteToast.svelte";
    import Form from "$components/Form.svelte";

	import { redirect } from '$utils/redirect';
    import { jwt } from "$stores/jwtStore";
    import { personIdStore } from "$stores/personIdStore";
    import { addToastByRes } from '$utils/addToToastStore';
    import { userPermissionsStore } from "$stores/userPermissionsStore";
    import { Validators} from "$utils/Validators";
    import { formFormat } from '$lib/types/formFormat';
    import FormError from '$lib/components/formError.svelte';
    
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
            validators: [Validators.required, Validators.minLength(8)],
        },
        email: {
            validators: [Validators.required, Validators.email],
        },
    };

    function handlePostFetch(data){
        let res = data.detail.res;
        if(res.success){
            $jwt = {token: res.token, username: username}
            $personIdStore = res.personId;
            $userPermissionsStore= res.permissions;
        }
        else{
            addToastByRes(res) 
        }
    }
</script>

<svelte:head>
	<link rel="icon" type="image/png" href={favicon}/>
	<title>Register</title>
    <script src="http://localhost:35729/livereload.js"></script>
</svelte:head>

<SvelteToast/>
<main class="flex justify-center items-center mx-auto h-screen text-white">
    <div class="rounded-xl shadow-2xl bg-slate-900 max-w-fit p-10">
        <div class="flex justify-center">
            <img class="flex justify-center w-24" src={favicon} alt="favicon"/>
            <h1 class="items-center text-3xl font-bold flex justify-center">Memori</h1>
        </div>
        <h1 class="font-bold text-2xl mx-auto flex justify-center pt-6 pb-2">Register</h1>
        <Form url="/api/register" method="POST" dataFormat={formFormat.FORM} {formValidators} on:postFetch={handlePostFetch}>
            <div class="flex flex-col gap-2">
                <div class="form-control">
                    <label class="input-group">
                    <span class="w-36">Username</span>
                    <input name="username" bind:value={username} type="text" placeholder="Max" class="input input-bordered w-full" />
                    </label>
                    <FormError name="username" key="required" message="Username is required"/>

                </div>
                <div class="form-control">
                    <label class="input-group">
                    <span class="w-36">Email</span>
                    <input name="email" type="email" placeholder="google@gmail.com" class="flex input input-bordered w-full" />
                    </label>
                    <FormError name="email" key="required" message="Email is required"/>
                    <FormError name="email" key="email" message="Email must be an official mail"/>
                </div>
                <div class="form-control">
                    <label class="input-group">
                    <span class="w-36">Password</span>
                    <input name="password" type="password" placeholder="1234" class="input input-bordered w-full" />
                    </label>
                    <FormError name="password" key="required" message="Password is required"/>
                    <FormError name="password" key="minLength" message="Password must be at least 8 characters"/>
                </div>
                <div class="flex justify-between">
                    <button type="button" class="btn btn-secondary" on:click={()=>redirect("login")}>Back</button>
                    <button type="submit" class="btn btn-primary">Register</button>
                </div>
            </div>
        </Form>
    </div>
</main>
