chrome.runtime.onInstalled.addListener(function() {
	chrome.browserAction.setBadgeText({text: ""});
    chrome.storage.sync.set({serverURL: 'http://localhost:1234'}, function() {
      console.log("Server storage init done");
    });
  });


chrome.runtime.onMessage.addListener((request, sender, sendResponse) => 
{
	chrome.storage.sync.get(['serverURL'], function(data) {
		$.ajax({
	  	        url: data.serverURL+"/cards/light/"+request.message
	  	        
	  	    }).then(function(data) {
	  	    	chrome.browserAction.setBadgeText({text: data.length+""});
	  	    	
	  	    	chrome.storage.sync.set({data: data}, function() {
	  	          console.log("Storage data done");
	  	        });
	  	    	
	  	    	sendResponse({result: data});
	  	    });
	});
    return true; // need for asynchronous call 
});