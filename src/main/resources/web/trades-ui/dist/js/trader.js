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


function buildImage(ged, classes)
{
	try{
		return "<img id='"+ged.id +"' src='data:image/"+ged.ext+";base64,"+ ged.data + "' class='img-fluid "+classes+" ' />";
		}catch(error)
		{
		return "<img id='none' src='../dist/img/none.png' class='img-fluid "+classes+" ' />";	
		}
		

}


function buildAnnounce(data, geds,xs,sm,md,lg)
{
		if(!data)
			return;
				var	img = buildImage(geds[0],"thumb-post");

				
				var append="<div class='col-xs-"+xs+" col-sm-"+sm+" col-md-"+md+" col-lg-"+lg+"'>";
				append+="<div class='featured-box'>";
				append+="<figure>";
					append+="<span class='price-save'> 5% Mkm </span>";
					append+="<div class='icon'><span class='bg-green'><i class='lni-heart'></i></span><span><i class='lni-bookmark'></i></span></div>";
					append+="<a href='announce.html?id="+data.id+"' >"+img+"</a>";
				append+="</figure>";
				append+="<div class='feature-content'>";
				append+="<div class='product'><i class='lni-check-box'></i> <a href='listAnnounces.html?type="+data.categorie+"'>"+data.categorie+"</a></div>";
				append+="<h4><a href='announce.html?id="+data.id+"'>"+data.title+"</a></h4>";
				append+="<div class='meta-tag'><span> <a href='#'><i class='lni-user'></i> "+data.contact.name + " " + data.contact.lastName +"</a>";
						append+="</span> <span> <a href='#'><i class='lni-map-marker'></i>"+data.contact.city+","+ data.contact.country+"</a>";
						append+="</span> <span> <a href='#'><i class='lni-alarm-clock'></i> "+data.endDate+"</a></span>";
				append+="</div>";
				
				append+="<p class='dsc'>"+data.description.substring(0, 50)+"</p>";
				append+="<div class='listing-bottom'>";
				append+="<h3 class='price float-left'>"+data.currencySymbol+" "+data.totalPrice+"</h3><a href='announce.html?id="+data.id+"' class='btn btn-common float-right'>View Details</a></div>";
				append+="</div></div></div>";
			return append;	
		
		
}