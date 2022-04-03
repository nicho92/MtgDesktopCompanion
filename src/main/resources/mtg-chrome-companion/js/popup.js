
chrome.storage.sync.get('data', function(d) {
	
	var scryfallId=0;
	
	for (var i = 0; i < d.data.length; i++) {
    	$('#content').append("<b>"+d.data[i].set+"</b>:");
    	
    	if(d.data[i].hasOwnProperty('scryfallId'))
    		{
    		scryfallId=d.data[0].scryfallId;
    		}
    	
    	
		for(var y =0; y<d.data[i].collections.length;y++)
		{
			$('#content').append(d.data[i].collections[y].name).append(",");
		}
		$('#content').append("<br/>");
   	}
	$('#cardPic').attr("src", "https://api.scryfall.com/cards/"+scryfallId+"?format=image");
	$('#cardname').append(d.data[0].name);
});