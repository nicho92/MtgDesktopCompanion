
function addCartProduct(stockItem)
{

	
	sessionStorage.setItem(stockItem.idstock, JSON.stringify(stockItem) );
	$("#cart").html(sessionStorage.length);
}



function addCartStockId(idstock)
{
	
     $.getJSON(restserver+"/stock/get/"+idstock,function(data) {
     				data.qte=1;
			    	sessionStorage.setItem(idstock, JSON.stringify(data) );
			    	 $("#cart").html(sessionStorage.length);
	 });
 	
}

function removeStockId(idstock)
{
     	sessionStorage.removeItem(idstock);
	   	 $("#cart").html(sessionStorage.length);
}


function clearCart()
{
     	sessionStorage.clear();
	   	$("#cart").html(sessionStorage.length);
}
	   
	   
function getCartItems()
{
		var array = [];
		for (var i = 0; i < sessionStorage.length; i++){
				array.push(JSON.parse(sessionStorage.getItem(sessionStorage.key(i))));
		}
		return array;
}

function createJSONOrder(contact) {
	
		var contactObj = {
			name:$(contact.get(0)).val(),
			lastName:$(contact.get(1)).val(),
			email:$(contact.get(2)).val(),
		}
	    
	    
	    
	    var jsonObj = {
	    	contact:contactObj,
	    	items : getCartItems(),
	    	message:$(contact.get(3)).val()
	    }
	  	
	  	return jsonObj;
}

