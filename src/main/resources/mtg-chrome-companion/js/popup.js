
chrome.storage.sync.get('data', function(data) {
	
	console.log(data);
	document.getElementById('content').html("c");
});