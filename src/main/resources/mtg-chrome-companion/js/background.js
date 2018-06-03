chrome.runtime.onInstalled.addListener(function() {
    chrome.storage.sync.set({serverURL: 'http://localhost:1234'}, function() {
      console.log("Server storage init done");
    });
  });


chrome.runtime.onMessage.addListener((request, sender, sendResponse) => 
{
	console.log(request);
	
	chrome.storage.sync.get(['serverURL'], function(data) {
		url=data.serverURL+"/cards/search/name/"+request.message;
		console.log(url);
	});
	
	sendResponse({message: 'ok'});
});