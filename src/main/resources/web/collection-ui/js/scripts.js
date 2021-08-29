
function mtgtooltip(element)
{
	element.popover({
        placement : 'top',
		trigger : 'hover',
        html : true,
        content: function () {
			
	        var scryfallid=$(this).attr("scryfallid");
			var multiverseId=$(this).attr("multiverseid");
			
			if(scryfallid != "undefined" && scryfallid!==undefined)
				uri = "https://api.scryfall.com/cards/"+scryfallid+"?format=image";
			else if(multiverseId!="undefined" && multiverseId!=undefined)            
				uri = "https://api.scryfall.com/cards/multiverse/"+multiverseId+"?format=image";
  
           return "<img class='img-responsive' src='"+uri+"'/>";
        }
    });
	
}

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
	return manaString.trim();
}





function tilt(ref)
{
		try{
			
			ref.tilt({
					scale: 1.2
			});
		
		}catch(error)
		{
			console.log(error + " " + JSON.stringify(ref));
		}
}

 function $_GET(param) {
	    	var vars = {};
	    	window.location.href.replace( location.hash, '' ).replace( 
	    		/[?&]+([^=&]+)=?([^&]*)?/gi, // regexp
	    		function( m, key, value ) { // callback
	    			vars[key] = value !== undefined ? value : '';
	    		}
	    	);

	    	if ( param ) {
	    		return vars[param] ? vars[param] : null;	
	    	}
	    	return vars;
	    }


function buildSet(collection, set)
{
	return  "<div class=''col mb-5''>"+
                        "<div class='card h-100'>"+
						    "<div class='card-body p-4'>"+
		                        "<div class='text-center'>"+
									"<a href='?collection="+collection+"&set="+set.id+"'' style='text-decoration:none' class='ss ss-"+set.id.toLowerCase()+"  ss-4x ss-common'></a>"+
                                    "<h5 class='fw-bolder''>"+set.set+"</h5>"+
                                   set.releaseDate+
                                "</div></div></div></div>";

}



function buildCollection(collection)
{
	return  "<div class=''col mb-5''>"+
                        "<div class='card h-100'>"+
						    "<div class='card-body p-4'>"+
		                        "<div class='text-center'>"+
                                    "<h5 class='fw-bolder''><em class='fa fa-2x	fa-book'><br/><a style='text-decoration:none' href='?collection="+collection.name+"'></em>"+collection.name+"</a></h5>"+
                                "</div></div></div></div>";

}

function buildCard(card)
{
			var url="https://api.scryfall.com/cards/"+card.scryfallId+"?format=image";
			if(!card.scryfallId)
				url = "https://api.scryfall.com/cards/multiverse/"+card.editions[0].multiverseId+"?format=image";
	
			return  "<div class='col mb-5'>"+
                       " <div class='card h-100'>"+
                         "  <img class='card-img-top' src='"+url+"' alt='"+card.name+"'>"+
                           " <div class='card-body p-5'>"+
                             "   <div class='text-center'>"+
                               "    <h5 class='fw-bolder'>"+card.name+"</h5>"+
                                		card.rarity+
                                " </div></div></div></div>";
	                        	
	
}



