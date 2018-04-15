
function formatMana(manaString)
{
	return manaString.replace(/\//g, '');	
}

function mtgtooltip(element)
{
	console.log(element);
	/*
	element.tooltip(
		        {
		            html: true,
		            trigger: "manual",
		            tooltipClass: "mytooltip"
		        }
		        ).on(
		        {
		            click:
		                function() {
		                    var $el = $(this);
		                    
		                    if( $el.data( "fetched" ) === undefined ) {
		                        $el.data( "fetched", true );
		                        $el.attr( "data-original-title", "<img src='../dist/img/loading.gif' width='50' height='50'>" ).tooltip( "show" );
		                        $.ajax(
		                            {
		                                url: restserver+"/pics/cards/"+idEd+"/"+$el.text(),
		                                success:
		                                    function( response ) {
		                                        $el.attr( "data-original-title", "<img src='"+restserver+"/pics/cards/"+ed+"/"+name+"' width='180' >" ).tooltip( "show" );
		                                      	
		                                    }
		                            }
		                            );
		                        
		                        
		                    } else {
		                        $(this).tooltip( "show" );
		                    }
		                },
		             mouseleave:
		                function() {
		                    $(this).tooltip( "hide" );
		                }
		        }
		        );	
*/
}
