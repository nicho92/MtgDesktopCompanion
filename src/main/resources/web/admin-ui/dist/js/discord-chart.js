var bigDashboard;
var tablediscordMsg;
var tablediscordGuilds;
var topUserChart;



const chartColor = "#FFFFFF";
		
server = {
  
    initDashboardPageCharts: function(data, start, end) {
		var datas = data.queries.filter(a => {
				 var date = moment(a.start);
				 	end = moment(end);
				 	start = moment(start);
				  return (date.isAfter(start) && date.isBefore(end));
		});

		var dayCounts = datas.reduce(function (result, d) {
								    var day = moment(d.start).format("YYYY-MM-DD HH");
								    if (!result[day]) {
								        result[day] = 0;
								    }
								    result[day]++;
								    return result;
									}, {});
		
		var endpointCount = datas.reduce(function (result, d) {
								    var u = d.user.name;
								     if (!result[u]) {
								        result[u] = 0;
								    }
								    result[u]++;
								    return result;
									}, {});
		
		var messageCount = datas.reduce(function (result, d) {
								    var u = d.message.match(/\{(.*)\}/)[1];
								  
								  	if(u.indexOf("|")>0)
								  		u=u.substring(u.indexOf("|")+1);
								  
								  	u=u.toLowerCase();
								  
								    if (!result[u]) {
								        result[u] = 0;
								    }
								    result[u]++;
								    return result;
									}, {});
		var words =[];
		
		
		
		$.each(messageCount, function( data, index) {
			 words.push( {text: data, weight: index});
		});
	
		$('#tagWords').jQCloud(words,{
		  shape: 'rectangular',
		  autoResize: true,
		  width: 500,
  		  height: 350
		}); 
	
	if(bigDashboard!=null){
		bigDashboard.destroy();
		topUserChart.destroy();
	}
	
 
    var ctx = document.getElementById('bigDashboardChart').getContext("2d");
  
    var gradientFill = ctx.createLinearGradient(0, 200, 0, 50);
    gradientFill.addColorStop(0, "rgba(128, 182, 244, 0)");
    gradientFill.addColorStop(1, "rgba(255, 255, 255, 0.24)");

    bigDashboard = new Chart(ctx, {
      type: 'line',
      data: {
        labels: Object.keys(dayCounts),
        datasets: [{
          label: "Data",
          borderColor: chartColor,
          pointBorderColor: chartColor,
          pointBackgroundColor: "#1e3d60",
          pointHoverBackgroundColor: "#1e3d60",
          pointHoverBorderColor: chartColor,
          pointBorderWidth: 1,
          pointHoverRadius: 7,
          pointHoverBorderWidth: 2,
          pointRadius: 5,
          fill: true,
          backgroundColor: gradientFill,
          borderWidth: 2,
          data: Object.values(dayCounts)
        }]
      },
      options: {
        layout: {
          padding: {
            left: 20,
            right: 20,
            top: 0,
            bottom: 0
          }
        },
        maintainAspectRatio: false,
        tooltips: {
          backgroundColor: '#fff',
          titleFontColor: '#333',
          bodyFontColor: '#666',
          bodySpacing: 4,
          xPadding: 12,
          mode: "nearest",
          intersect: 0,
          position: "nearest"
        },
        legend: {
          position: "bottom",
          fillStyle: "#FFF",
          display: false
        },
        scales: {
          yAxes: [{
            ticks: {
              fontColor: "rgba(255,255,255,0.4)",
              fontStyle: "bold",
              beginAtZero: true,
              maxTicksLimit: 5,
              padding: 10
            },
            gridLines: {
              drawTicks: true,
              drawBorder: false,
              display: true,
              color: "rgba(255,255,255,0.1)",
              zeroLineColor: "transparent"
            }

          }],
          xAxes: [{
            gridLines: {
              zeroLineColor: "transparent",
              display: false,

            },
            ticks: {
              padding: 10,
              fontColor: "rgba(255,255,255,0.4)",
              fontStyle: "bold"
            }
          }]
        }
      }
    });
	
	
	var e = document.getElementById("topUserChart").getContext("2d");
    gradientFill = ctx.createLinearGradient(0, 170, 0, 50);
    gradientFill.addColorStop(0, "rgba(128, 182, 244, 0)");
    gradientFill.addColorStop(1, hexToRGB('#2CA8FF', 0.6));

    var a = {
      type: "bar",
      data: {
        labels: Object.keys(endpointCount),
        datasets: [{
          backgroundColor: gradientFill,
          borderColor: "#2CA8FF",
          pointBorderColor: "#FFF",
          pointBackgroundColor: "#2CA8FF",
          pointBorderWidth: 2,
          pointHoverRadius: 4,
          pointHoverBorderWidth: 1,
          pointRadius: 4,
          fill: true,
          borderWidth: 1,
          data: Object.values(endpointCount)
        }]
      },
      options: {
        maintainAspectRatio: false,
        legend: {
          display: false
        },
        tooltips: {
          bodySpacing: 4,
          mode: "nearest",
          intersect: 0,
          position: "nearest",
          xPadding: 10,
          yPadding: 10,
          caretPadding: 10
        },
        responsive: 1,
        scales: {
          yAxes: [{
            gridLines: 0,
			gridLines: {
              zeroLineColor: "transparent",
              drawBorder: false
            }
          }],
          xAxes: [{
            display: true,
            gridLines: 0,
            ticks: {
              display: true
            },
            gridLines: {
              zeroLineColor: "transparent",
              drawTicks: false,
              display: false,
              drawBorder: false
            }
          }]
        },
        layout: {
          padding: {
            left: 0,
            right: 0,
            top: 15,
            bottom: 15
          }
        }
      }
    };

   topUserChart =  new Chart(e, a);
	
	
	
	
	
	
	if(tablediscordGuilds==null)
	{
		tablediscordGuilds =$('#tablediscordGuilds').DataTable({
                  'data' : data.server.guilds,
				  "responsive": true,
                  'columns' : [
                      {
                          'data' : 'name',
                          render : function(d,type,row){
	
								if(type === 'display'){
									if(row.icon!=null)
		                              return "<img class='rounded img-fluid ' src='"+row.icon+"'>"+d;
		                            else
		                            	return "<img class='rounded' height='128px' width='128px' src='https://static.thenounproject.com/png/1455872-200.png'>"+d;
                              	}
                              	return d;
                          }
                        }
                  ]

        });
	}
	else
	{
		tablediscordGuilds.clear().rows.add(data.server.guilds).draw();
		
	}
	
	
	
	
	if(tablediscordMsg==null)
	{ 
		tablediscordMsg =$('#tablediscordMsg').DataTable({
                  'data' : datas,
				  'order': [[ 2, "desc" ]],
				  "responsive": true,
                  'columns' : [
                      {
					   'data' : 'user.name',
 					   'defaultContent': ""
					},
                      {
					   'data' : 'guild.name',
 					   'defaultContent': ""
					},
                      {
                          'data' : 'start',
                          render : function(d,type,row){
                            if(type === 'display'){
                              	 return moment(d).format('DD MMM YYYY, HH:mm:ss');
                              	 }
                              	 else
                              	 {
									return d;
									}
                          }
                        },
                      {'data' : 'duration'},
                      {'data' : 'message'}
                      
                      
                  ]

        });
	}
	else
	{
		tablediscordMsg.clear().rows.add(datas).draw();
		
	}
	
	
  }
};