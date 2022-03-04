
function formatMana(manaString)
{
	if(manaString.includes("/P"))
	{
		return manaString.replace(/\//g, '');
	}
	else if (manaString.includes("/"))
	{
		var s = manaString.replace(/\//g, '');
		s += " ms-split ";
		return s;
	}
	return manaString;
}



function mtgtooltip(element)
{
	element.popover({
        placement : 'top',
		trigger : 'hover',
        html : true,
        content: function () {
	
	        var set = $(this).attr("data-set");
            var name=$(this).attr("data-name");
			var scryfallid=$(this).attr("scryfallid");
			var multiverseId=$(this).attr("multiverseid");
			var uri=restserver+"/pics/cards/"+set+"/"+name;
			
			if(scryfallid != "undefined" && scryfallid!==undefined)
				uri = "https://api.scryfall.com/cards/"+scryfallid+"?format=image";
			else if(multiverseId!="undefined" && multiverseId!=undefined)            
				uri = "https://api.scryfall.com/cards/multiverse/"+multiverseId+"?format=image";
  
           return "<img class='img-fluid' src='"+uri+"'/>";
        }
    });
	
}


function mtgtooltipStock(element)
{
	element.popover({
        placement : 'top',
		trigger : 'hover',
        html : true,
        content: function () {
            return '<img width="250px" src="'+$(this).attr("productUrl")+'"/>';
        }
    });
	
}