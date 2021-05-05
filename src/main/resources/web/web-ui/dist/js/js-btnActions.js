function addCard(idCard,to,callback)
{
	$.ajax({
		type: 'PUT',
	    url: restserver+"/cards/add/"+to+"/"+idCard
   		 }).done(function(data) {
   			callback();
   		 }).fail(function(data,status,error) {
   			alert(JSON.stringify(data) + " " + error);
   		 });
	
}

function addCardtoDefaultLibrary(idCard,callback)
{
	$.ajax({
		type: 'PUT',
	    url: restserver+"/cards/add/"+idCard
   		 }).done(function(data) {
   			callback();
   		 }).fail(function(data,status,error) {
   			alert(JSON.stringify(data) + " " + error);
   		 });
	
}

function moveCard(idCard,from, to,callback)
{
	$.ajax({
		type: 'PUT',
	    url: restserver+"/cards/move/"+from+"/"+to+"/"+idCard
   		 }).done(function(data) {
   			callback();
   		 }).fail(function(data,status,error) {
   			alert(JSON.stringify(data) + " " + error);
   		 });
}


function addAlert(idCard,callback)
{
	$.ajax({
		type: 'PUT',
	    url: restserver+"/alerts/add/"+idCard
   		 }).done(function(data) {
   			callback();
   		 }).fail(function(data,status,error) {
   			alert(JSON.stringify(data) + " " + error);
   		 });
}


function addStock(idCard,callback)
{
	$.ajax({
		type: 'PUT',
	    url: restserver+"/stock/add/"+idCard
   		 }).done(function(data) {
   			callback();
   		 }).fail(function(data,status,error) {
   			alert(JSON.stringify(data) + " " + error);
   		 });

}

function addCollection(name,callback)
{
	$.ajax({
		type: 'PUT',
	    url: restserver+"/collections/add/"+name
   		 }).done(function(data) {
   			callback();
   		 }).fail(function(data,status,error) {
   			alert(JSON.stringify(data) + " " + error);
   		 });

}

