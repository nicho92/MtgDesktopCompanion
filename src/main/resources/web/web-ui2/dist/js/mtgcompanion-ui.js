
function formatMana(manaString)
{
	if(manaString.includes("/P"))
	{
		return manaString.replace(/\//g, '');
	}
	else if (manaString.includes("/"))
	{
		var s = manaString.replace(/\//g, '');
		s += " ms-split ";
		return s;
	}
	return manaString;
}



function mtgtooltip(element)
{
	element.popover({
        placement : 'top',
		trigger : 'hover',
        html : true,
        content: function () {
	
	        var set = $(this).attr("data-set");
            var name=$(this).attr("data-name");
			var scryfallid=$(this).attr("scryfallid");
			var multiverseId=$(this).attr("multiverseid");
			var uri=restserver+"/pics/cards/"+set+"/"+name;
			
			if(scryfallid != "undefined" && scryfallid!==undefined)
				uri = "https://api.scryfall.com/cards/"+scryfallid+"?format=image";
			else if(multiverseId!="undefined" && multiverseId!=undefined)            
				uri = "https://api.scryfall.com/cards/multiverse/"+multiverseId+"?format=image";
  
           return "<img class='img-fluid' src='"+uri+"'/>";
        }
    });
	
}


function mtgtooltipStock(element)
{
	element.popover({
        placement : 'top',
		trigger : 'hover',
        html : true,
        content: function () {
            return '<img width="250px" src="'+$(this).attr("productUrl")+'"/>';
        }
    });
	
}


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


function printBarChart(ctx,typeChart,keys,values,displayLegend,color, scales)
{
	new Chart(ctx, {
      type: typeChart,
      data: {
        labels: keys,
        datasets: [{
          label: "Types",
          tension: 0,
          borderWidth: 0,
          pointRadius: 5,
          pointBackgroundColor: "rgba(255, 255, 255, .8)",
          pointBorderColor: "transparent",
          borderColor: "rgba(255, 255, 255, .8)",
          borderColor: "rgba(255, 255, 255, .8)",
          borderWidth: 4,
          backgroundColor: color,
          fill: true,
          data: values,
          maxBarThickness: 6

        }],
      },
      options: {
        responsive: true,
        maintainAspectRatio: false,
        plugins: {
          legend: {
            display: displayLegend,
          }
        },
        interaction: {
          intersect: false,
          mode: 'index',
        },
        scales: {
          y: {
            grid: {
              drawBorder: false,
              display: true,
              drawOnChartArea: true,
              drawTicks: false,
              borderDash: [5, 5],
              color: 'rgba(255, 255, 255, .2)'
            },
            ticks: {
              display: true,
              color: '#f8f9fa',
              padding: 10,
              font: {
                size: 14,
                weight: 300,
                family: "Roboto",
                style: 'normal',
                lineHeight: 2
              },
            }
          },
          x: {
            grid: {
              drawBorder: false,
              display: false,
              drawOnChartArea: false,
              drawTicks: false,
              borderDash: [5, 5]
            },
            ticks: {
              display: true,
              color: '#f8f9fa',
              padding: 10,
              font: {
                size: 14,
                weight: 300,
                family: "Roboto",
                style: 'normal',
                lineHeight: 2
              },
            }
          },
        }
		},
    });
}


function printChart(ctx, typeChart, label, keys,values, displayLegend, colors)
{
	return  new Chart(ctx, {
      type: typeChart,
      data: {
        labels: keys,
        datasets: [{
          label: label,
          tension: 0,
          borderWidth: 0,
          pointRadius: 5,
          pointBackgroundColor: "rgba(255, 255, 255, .8)",
		  backgroundColor: colors,
          pointBorderColor: "transparent",
          borderColor: "rgba(255, 255, 255, .8)",
          borderWidth: 4,
          fill: true,
          data: values,
          maxBarThickness: 6
        }],
      },
      options: {
        responsive: true,
        maintainAspectRatio: false,
        plugins: {
          legend: {
            display: displayLegend,
          }
        },
        interaction: {
          intersect: false,
          mode: 'index',
        },
      },
    });


}



