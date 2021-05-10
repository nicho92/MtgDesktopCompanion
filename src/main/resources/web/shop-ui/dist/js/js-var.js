 function $_GET(param) 
 {
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
	    

function generateEditionHTML(data)
{
				var append="<div class='col-md-2'>";
           			append+="<p class='card-text'><i class='ss ss-3x ss-"+data.keyRuneCode.toLowerCase()+"'></i>"+data.set+"</p>";
                	append+="</div>";
                	
         return append;
}

	   
function generateStockCardHTML(data,currency, tosell, percentReduction)
{

			var append="<div class='col-sm'>";
  					append+="<div class='card'>";
        			
        			
        			if(data.magicCard.scryfallId!=null)
    					append+="<img class='card-img-top' src='https://api.scryfall.com/cards/"+data.magicCard.scryfallId+"?format=image' alt='Card image cap'>";
    				else
    					append+="<img class='card-img-top' src='https://api.scryfall.com/cards/multiverse/"+data.magicCard.editions[0].multiverseId+"?format=image' alt='Card image cap'>";
    				
       				append+="<div class='card-body'>";
           			
           			append+="<h5 class='card-title'><a href='product.html?id="+data.idstock+"' title='View Product'>"+data.magicCard.name +"</a></h5>";
           				
           			append+="<p class='card-text'>"+data.magicCard.editions[0].set+"</p>";
           			append+="<div class='row'>";
                	append+="<div class='col'>";
                	
					if(percentReduction>0)
                		append+="<p class='btn btn-danger btn-block'>"+(data.price-(data.price*percentReduction)).toFixed(2)+" " + currency +  "</p>";
                	else
                		append+="<p class='btn btn-danger btn-block'>"+data.price.toFixed(2)+" " + currency +  "</p>";
                		
                		
                	append+="</div>";
               		append+="<div class='col'>";
               		
               			
               		if(tosell===true)
               		{
               			append+="<button name='addCartButton' data='"+ data.idstock+"' class='btn btn-success btn-block' sell='true' ><i class='fa fa-shopping-cart'></i> Deal it</button>";
               		}
               		else
               		{
               		               		
               		if(data.qte>=1)                                       		
                    	append+="<button name='addCartButton' data='"+ data.idstock+"' class='btn btn-success btn-block'><i class='fa fa-shopping-cart'></i> Add to cart</button>";
                    else
                    	append+="<span class='btn btn-secondary btn-block'><i class='fa fa-shopping-cart'></i>Out of stock</span>";
                    	
               		
               		}
               		
                    	
                    	
                    	
                	append+="</div></div></div></div></div>";
              
                	
                	
         return append;

}
