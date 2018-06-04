
chrome.storage.sync.get('data', function(d) {
	
	$('#content').append("<h3>"+d.data[0].name+"<h3>");
	
	for (var i = 0; i < d.data.length; i++) {
    	$('#content').append("<b>"+d.data[i].set+"</b>:");
		for(var y =0; y<d.data[i].collections.length;y++)
		{
			$('#content').append(d.data[i].collections[y].name).append(",");
		}
		$('#content').append("<br/>");
   	}
	$('#cardPic').attr("src", "http://gatherer.wizards.com/Handlers/Image.ashx?multiverseid="+d.data[0].multiverse+"&type=card");
});