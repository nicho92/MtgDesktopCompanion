
function formatMana(manaString)
{
	return manaString.replace(/\//g, '');	
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
            var uri = "<img src='"+restserver+"/pics/cards/"+set+"/"+name+"'>";
            
            if(set===undefined)
            	{
            	uri = "<img src='"+restserver+"/pics/cardname/"+name+"'>";
            	}
            
            
            return uri;
        }
    });
	
}
