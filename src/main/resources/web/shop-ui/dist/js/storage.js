
var storage = sessionStorage; // or localStorage
var cartKey = "cart";
var userKey = "user";
var configKey = "config";


/////////////CONFIG

function getConfig()
{

	if(!sessionStorage.getItem(configKey)){
		$.getJSON(restserver+"/webshop/config",function(data) {
			sessionStorage.setItem(configKey,JSON.stringify(data));
		 });
	 }
	 JSON.parse(sessionStorage.getItem(configKey));
	
}


/////////////USER

function storeUser( user )
{
	storage.setItem(userKey,JSON.stringify(user));
}

function getCurrentUser()
{
	if(storage.getItem(userKey))
		return JSON.parse(localStorage.getItem(userKey));
	
	return null;
}


function logout()
{
	storage.removeItem(userKey);
}


/////////////CART


function addCartProduct(stockItem, percentReduction)
{
	var array = JSON.parse(storage.getItem(cartKey) || "[]");
	var it=	array.find(x => x.idstock == stockItem.idstock);
	
	if(percentReduction>0)
		stockItem.price = stockItem.price-(stockItem.price*percentReduction);
	
	if(it)
		it.qte = it.qte+1;
	else
		array.push(stockItem);
	
	
	storage.setItem(cartKey, JSON.stringify(array) );
	$("#cart").html(array.length);
}



function addCartStockId(idstock, toSell,percentReduction)
{
    $.getJSON(restserver+"/stock/get/"+idstock,function(data) {
			data.qte=1;
			
			if(toSell=='true')
				data.price = -data.price;
			
			addCartProduct(data,percentReduction);
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
	   	$("#checkoutBtn").prop('disabled',true);
}
	   
	   
function getCartItems()
{
		return JSON.parse(storage.getItem(cartKey) || "[]");
}

function createJSONOrder(msg) {
	
		var jsonObj = {
	    	contact:getCurrentUser(),
	    	items : getCartItems(),
	    	message:escape(msg)
	    	
	    }
	  	
	  	return jsonObj;
}

