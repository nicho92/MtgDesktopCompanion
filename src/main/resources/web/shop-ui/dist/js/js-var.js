
 	
    function SortByDate(x,y,order) {
   		return x.id - y.id;
    }
    
    function SortByDateDesc(x,y,order) {
   		return y.id - x.id;
    }

    function SortByPrice(x,y) {
   		return x.price - y.price;
    }
    
    function SortByPriceDesc(x,y) {
   		return y.price - x.price;
    }
    
    function SortByName(x,y) {
      	return ((x.name == y.name) ? 0 : ((x.name > y.name) ? 1 : -1 ));
    }
    
    


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

function replaceMana(content)
{
  if(content==null)
  return content;


  //change linebreak
	content = content.replace(/(?:\r\n|\r|\n)/g, '<br>');
	
	content=content.replace(/\{T\}/g,'<i class="ms ms-tap ms-cost ms-shadow"></i>');
	
	
	//change keyword by mana symbol
	content=content.replace(/\{(.*?)\}/g,'<i class="ms ms-$1 ms-cost ms-shadow"></i>').toLowerCase();
	
	
	
	return content;
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

			if(!data)
				return;
			
			var append="<div class='col-sm'>";
  					append+="<div class='card'>";
					append+="<img class='card-img-top' src='"+data.url+"'>";
    				append+="<div class='card-body'>";
           			
           			
           			var type="SEALED";
           				if(data.product.typeProduct=="CARD")
           					type="CARD";
           			
           			
           			append+="<h5 class='card-title'><a href='product.html?id="+data.id+"&product="+type+"' title='View Product'>"+data.product.name +"</a></h5>";
           				
           			append+="<p class='card-text'>";
							append+="<i class='ss ss-2x ss-"+data.edition.id.toLowerCase()+"'></i>";
							append+= data.condition + " " + (data.foil?"<i class='fas fa-star fa-1x'/>":"") ;
							
					append+="</p>";
							
           			append+="<div class='row'>";
                	append+="<div class='col'>";
                	
					if(percentReduction>0)
                		append+="<p align='center' class='rounded p-2 mb-2 bg-danger text-white'><strong>"+(data.price-(data.price*percentReduction)).toFixed(2)+" " + currency +  "</strong></p>";
                	else
                		append+="<p align='center' class='rounded p-2 mb-2 bg-danger text-white'><strong>"+data.price.toFixed(2)+" " + currency +  "</strong></p>";
                		
                		
                	append+="</div>";
               		append+="<div class='col'>";
               		
               			
               		if(tosell===true)
               		{
               			append+="<button name='addCartButton' data-dismiss='alert'  data='"+ data.id+"' type='"+data.product.typeProduct+"' class='btn btn-success btn-block' sell='true' ><i class='fa fa-shopping-cart'></i> Deal it</button>";
               		}
               		else
               		{
               		               		
               		if(data.qte>=1)                                       		
                    	append+="<button name='addCartButton' qty='"+ data.qte +"' data='"+ data.id+"' type='"+data.product.typeProduct+"' class='btn btn-success btn-block'><i class='fas fa-cart-plus' ></i> Add to cart </button>";
                    else
                    	append+="<span class='btn btn-secondary btn-block'><i class='fa fa-shopping-cart'></i>Out of stock</span>";
                    	
               		
               		}
                	append+="</div></div></div></div></div>";
         return append;

}
