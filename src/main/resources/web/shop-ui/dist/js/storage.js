
var storage = sessionStorage; // or localStorage
var cartKey = "cart";
var userKey = "user";
var configKey = "config";


/////////////CONFIG
$.ajaxSetup({cache:true});


function getConfig()
{
		var d;
		$.ajax({
			url: restserver+"/webshop/config",
			async:false,
			success: function(data)
			{
				d=data;
			}
			});
        
		return  d;
}


/*function getConfig()
{
	if(!storage.getItem(configKey))
	{
		$.ajax({
			url: restserver+"/webshop/config",
			async:false
			}).done(function(data) {
			storage.setItem(configKey,JSON.stringify(data));
			return data;
        });
	}
	return JSON.parse(storage.getItem(configKey));
}
*/

/////////////USER

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
	storage.removeItem(userKey);
}


/////////////CART



function addCartStockId(btn,percentReduction)
{
	
	var serv=btn.attr("type");
	
	if(serv=="CARD")
		serv="stock";
	else
		serv="sealed";
	
	 $.getJSON(restserver+"/"+serv.toLowerCase()+"/get/"+btn.attr("data"),function(data) {
			data.qte=1;
			
			if(btn.attr("sell")=='true')
				data.price = -data.price;
			
			addCartProduct(data,percentReduction);
			$('.alert').alert();
	 });
}


function addCartProduct(stockItem, percentReduction)
{
	var array = JSON.parse(storage.getItem(cartKey) || "[]");
	var it=	array.find(x => x.id == stockItem.id);
	
	if(percentReduction>0)
		stockItem.price = stockItem.price-(stockItem.price*percentReduction);
	
	if(it)
		it.qte = it.qte+1;
	else
		array.push(stockItem);
	
	
	storage.setItem(cartKey, JSON.stringify(array) );
	$("#cart").html(array.length);
}



function removeStockId(idstock)
{

		var array = jQuery.grep(getCartItems(), function(value) {
			 return value.id != idstock;
		});


     	 storage.setItem(cartKey, JSON.stringify(array) );
	   	 $("#cart").html(array.length);
}


function isPresent(idstock)
{

	return getCartItems().find(x => x.id == idstock);
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
	    	message:jQuery('<p>' + msg + '</p>').text(),
	    	shippingPrice:eval(getConfig().shippingRules)
	    }
	  	
	  	return jsonObj;
}

