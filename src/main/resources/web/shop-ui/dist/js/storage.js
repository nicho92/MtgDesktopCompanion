
var storage = sessionStorage; // or localStorage
var cartKey = "cart";
var userKey = "user";



function storeUser( user )
{
	storage.setItem(userKey,JSON.stringify(user));
}

function getCurrentUser()
{
	if(storage.getItem(userKey))
		return JSON.parse(storage.getItem(userKey));
	
	return null;
}


function logout()
{
	storage.setItem(userKey,"");
}



function addCartProduct(stockItem)
{
	var array = JSON.parse(storage.getItem(cartKey) || "[]");
	var it=	array.find(x => x.idstock == stockItem.idstock);
	
	if(it)
		it.qte = it.qte+1;
	else
		array.push(stockItem);
	
	storage.setItem(cartKey, JSON.stringify(array) );
	$("#cart").html(array.length);
}



function addCartStockId(idstock, toSell)
{
    $.getJSON(restserver+"/stock/get/"+idstock,function(data) {
			data.qte=1;
			if(toSell=='true')
				data.price = -data.price;
	
			addCartProduct(data);
	 });
}

function removeStockId(idstock)
{

		var array = jQuery.grep(getCartItems(), function(value) {
			 return value.idstock != idstock;
		});


     	 storage.setItem(cartKey, JSON.stringify(array) );
	   	 $("#cart").html(array.length);
}


function isPresent(idstock)
{

	return getCartItems().find(x => x.idstock == idstock);
}



function clearCart()
{
     	storage.setItem(cartKey,"[]");
	   	$("#cart").html(getCartItems().length);
}
	   
	   
function getCartItems()
{
		return JSON.parse(storage.getItem(cartKey) || "[]");
}

function createJSONOrder(msg) {
	
		var jsonObj = {
	    	contact:getCurrentUser(),
	    	items : getCartItems(),
	    	message:msg
	    }
	  	
	  	return jsonObj;
}

