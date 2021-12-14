
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


