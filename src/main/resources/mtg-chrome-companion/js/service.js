document.addEventListener('mouseup', function (e) {
  var selection = window.getSelection().toString();
  
  if (selection.length > 0 && selection.length < 30) {
	  chrome.runtime.sendMessage({message: selection}, (response) => {
		  console.log(response);
		 
		  
	  });
  	}
}, false);
