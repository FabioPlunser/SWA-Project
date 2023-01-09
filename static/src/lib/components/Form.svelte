<script lang="ts">
    /**
     * @param {string} dataFormat - "JSON" or "FormData"
    */
    import { createEventDispatcher } from "svelte";
    import { fetching } from "../utils/fetching";
    import { tokenStore } from "../stores/tokenStore";

    let dispatch = createEventDispatcher();
    import { validateForm, isFormValid } from "../utils/Validators";

    export let style = "";
    export let dataFormat = "JSON";
    export let url = "";
    export let method = "";
    export let addJSONData = [{}];
    export let errors = {};
    export let formValidators = {};
    export let id = "";


    let myHeaders = new Headers();
    myHeaders.append("Authorization", "Bearer " + $tokenStore);


    async function handleSubmit(e) {
        let res;
        const formData = new FormData(e.target);

        errors = validateForm(e, formValidators);
        if(!isFormValid(errors)){
            return;
        }
                
        dispatch("preFetch", e);

        if(dataFormat === "JSON"){
            let data = formData;
            for (const [key, value] of data.entries()) {
                data[key] = value;
            }
            for(let JSONData of addJSONData){
                data = {...data, ...JSONData};
            }

            res = await fetching(url, method, null, data, true);
            dispatch("postFetch", {e, res});
            return res;
        }else{
            res = await fetching(url, method, null, formData, false);
            dispatch("postFetch", {e, res});
            return res;
        }
    }
</script>

<!-- @component
    A form component that handles validation and fetching
    @param {string} url - URL to fetch
    @param {string} method - method to use for fetching
    @param {string} dataFormat - "JSON" or "FormData" standard is JSON
    @param {array} addJSONData - [{key: value}] to add to the json data only if dataFormat is JSON
    @param {object} errors - {key: value} to store errors
    @param {object} formValidators - {key: value} validators for the form
    @param {number} id - id of the form
    @param {function} preFetch - dispatch event before fetching
    @param {function} postFetch - dispatch event after fetching
 -->
<form class={style} {id} action={url} {method} on:submit|preventDefault={handleSubmit}>
    <slot/>
</form>