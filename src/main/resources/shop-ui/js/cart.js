
function addCartProduct(stockItem)
{
	sessionStorage.setItem(stockItem.idstock, JSON.stringify(stockItem) );
	$("#cart").html(sessionStorage.length);
}



function addCartStockId(idstock)
{
     $.getJSON(restserver+"/stock/get/"+idstock,function(data) {
			    	sessionStorage.setItem(idstock, JSON.stringify(data) );
			    	 $("#cart").html(sessionStorage.length);
	 });
 	
}

function removeStockId(idstock)
{
     	sessionStorage.removeItem(idstock);
	   	 $("#cart").html(sessionStorage.length);
	
 	
}
	    