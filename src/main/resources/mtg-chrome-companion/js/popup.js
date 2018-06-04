
chrome.storage.sync.get('data', function(d) {
	
	var idMultiverse=0;
	
	for (var i = 0; i < d.data.length; i++) {
    	$('#content').append("<b>"+d.data[i].set+"</b>:");
    	
    	if(d.data[i].hasOwnProperty('multiverse'))
    		{
    		idMultiverse=d.data[0].multiverse;
    		}
    	
    	
		for(var y =0; y<d.data[i].collections.length;y++)
		{
			$('#content').append(d.data[i].collections[y].name).append(",");
		}
		$('#content').append("<br/>");
   	}
	$('#cardPic').attr("src", "http://gatherer.wizards.com/Handlers/Image.ashx?multiverseid="+idMultiverse+"&type=card");
	$('#cardname').append(d.data[0].name);
});