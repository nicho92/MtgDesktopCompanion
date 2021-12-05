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


function buildAnnounce(data)
{
		if(!data)
			return;
				
				var img = "<img src='../dist/img/none.png' class='img-fluid thumb-post' alt="+data.title+"/>";
				
				
				var append=	"<div class='col-xs-6 col-sm-6 col-md-6 col-lg-4'>";
				append+="<div class='featured-box'>";
				append+="<figure>";
					append+="<span class='price-save'> 5% Mkm </span>";
					append+="<div class='icon'><span class='bg-green'><i class='lni-heart'></i></span><span><i class='lni-bookmark'></i></span></div>";
					append+="<a href='announce.html?id="+data.id+"' >"+img+"</a>";
				append+="</figure>";
				append+="<div class='feature-content'>";
				append+="<div class='product'><a href='#'>Sealed > </a> <a href='#'>Booster Box</a></div>";
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