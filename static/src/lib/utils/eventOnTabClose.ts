export const eventOnTabClose = (_, callback) => {
    window.addEventListener("beforeunload", function(e){
		callback();
	}, false);
}
