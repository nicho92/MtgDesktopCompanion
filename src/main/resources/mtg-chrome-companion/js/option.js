let server = document.getElementById('txtServer');
let btnSave = document.getElementById('btnsave');


chrome.storage.sync.get(['serverURL'], function(data) {
	document.getElementById('txtServer').value=data.serverURL;
});


btnSave.addEventListener('click', function() {
chrome.storage.sync.set({serverURL: server.value}, function() {
	console.log('save serveur to ' + server.value);
    })
});
  
