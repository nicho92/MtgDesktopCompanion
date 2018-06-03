chrome.runtime.onInstalled.addListener(function() {
    chrome.storage.sync.set({serverURL: 'http://localhost:1234'}, function() {
      console.log("Server storage init done");
    });
  });

chrome.runtime.onMessage.addListener((request, sender, sendResponse) => 
{
      console.log(request.message);
      
      
      
      
      sendResponse({message: "hi to you"});
});