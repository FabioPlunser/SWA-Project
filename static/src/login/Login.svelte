<script lang="ts">
    // TODO implement login locig and page redirection
    import Nav from "../lib/components/nav.svelte";
    import {loggedIN} from "../lib/stores/loggedIn";

    let username = "";
    async function handleSubmit (e){
		// getting the action url
		const ACTION_URL = e.target.action

		// get the form fields data and convert it to URLSearchParams
		const formData = new FormData(e.target)
		const data = new URLSearchParams()
		for (let field of formData) {
			const [key, value] = field
			data
		}

		// check the form's method and send the fetch accordingly
		if (e.target.method.toLowerCase() == 'get') fetch(`${ACTION_URL}?${data}`)
		else {
			let res = await fetch(ACTION_URL, {
				method: 'POST',
				body: data
			})
            res = await res.json()
            console.log(res);
            if(res.success = false){
                alert(res.message)
            }
		}
	}
</script>

<svelte:head>
	<link rel="icon" type="image/png" href="../static/882998.png"/>
	<title>Login</title>
</svelte:head>


<main class="flex justify-center items-center mx-auto h-screen text-white">
    <div class="rounded-xl shadow-2xl bg-slate-900 max-w-fit p-10">
        <h1 class="underline text-2xl mx-auto flex justify-center p-2">Login</h1>
        <form method="POST" action="api/register" on:submit|preventDefault={handleSubmit}>
            <div class="form-control">
                <label class="input-group">
                  <span>Username</span>
                  <input name="username" required type="text" placeholder="Max" class="input input-bordered" />
                </label>
            </div>
            <br class="pt-4"/>
            <div class="form-control">
                <label class="input-group">
                  <span>Email</span>
                  <input name="email" required type="text" placeholder="test@example" class="input input-bordered" />
                </label>
            </div>
            <br class="pt-4"/>
            <div class="form-control">
                <label class="input-group">
                  <span>Password</span>
                  <input name="password" required type="text" placeholder="1234" class="input input-bordered" />
                </label>
            </div>
            <div class="flex justify-between pt-4">
                <button type="submit" class="btn btn-primary">Login</button>
                <!-- <button class="btn btn-primary">Register</button> -->
            </div>
        </form>
    </div>
</main>
