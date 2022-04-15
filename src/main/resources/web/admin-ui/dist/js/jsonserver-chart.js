var tableEndPoint;
var bigDashboard;
var deviceRepartitionChart;
var browserrepartitionchart;
var hourRepartitionChart;
var requestedEndpoint;


const chartColor = "#FFFFFF";

const CHART_COLORS = [
			  'rgb(80, 200, 120)',
			  'rgb(0, 173, 239)',
			  'rgb(230, 96, 0)',
			  'rgb(255, 203, 0)',
			  'rgb(54, 162, 235)',
			  'rgb(153, 102, 255)',
			  'rgb(201, 203, 207)'
			];

var  gradientChartOptionsConfiguration = {
      maintainAspectRatio: false,
      responsive: true,
      legend: {
        display: true
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
          display: 0,
          gridLines: 0,
          ticks: {
            display: false
          },
          gridLines: {
            zeroLineColor: "transparent",
            drawTicks: false,
            display: false,
            drawBorder: false
          }
        }],
        xAxes: [{
          display: 0,
          gridLines: 5,
          ticks: {
            display: false
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
    };

var  gradientChartOptionsConfigurationWithNumbersAndGrid = {
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
      responsive: true,
      scales: {
        yAxes: [{
          gridLines: 0,
          gridLines: {
            zeroLineColor: "transparent",
            drawBorder: false
          }
        }],
        xAxes: [{
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
    };
	
	
		
server = {
  
    initDashboardPageCharts: function(data, start, end) {
		var datas = data.filter(a => {
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
	
	
		var deviceCount = datas.reduce(function (result, d) {
								    var device = d.userAgent.DeviceClass
								    if (!result[device]) {
								        result[device] = 0;
								    }
								    result[device]++;
								    return result;
									}, {});
									
		var browserCount = datas.reduce(function (result, d) {
								    var device = d.userAgent.AgentName
								    if (!result[device]) {
								        result[device] = 0;
								    }
								    result[device]++;
								    return result;
									}, {});
	
		var hourCount = datas.reduce(function (result, d) {
								    var hour = moment(d.start).format('HH')+"H";
								    if (!result[hour]) {
								        result[hour] = 0;
								    }
								    result[hour]++;
								    return result;
									}, {});
	
		var endpointCount = datas.reduce(function (result, d) {
								    var uri = d.url.substring(0,d.url.lastIndexOf("/"));
								     if (!result[uri]) {
								        result[uri] = 0;
								    }
								    result[uri] += d.duration;
								    return result;
									}, {});

		var countriesCount = [...new Map(datas.map(item =>[item['ip'], item.location])).values()];
	
	
	if(bigDashboard!=null){
		bigDashboard.destroy();
		deviceRepartitionChart.destroy();
		browserrepartitionchart.destroy();
		hourRepartitionChart.destroy();
		requestedEndpoint.destroy();
	}
	
 
    var ctx = document.getElementById('bigDashboardChart').getContext("2d");

    var gradientStroke = ctx.createLinearGradient(500, 0, 100, 0);
    gradientStroke.addColorStop(0, '#80b6f4');
    gradientStroke.addColorStop(1, chartColor);

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

	
    deviceRepartitionChart = new Chart( document.getElementById('deviceRepartitionChart').getContext("2d"), {
      type: 'doughnut',
      responsive: true,
      data: {
        labels: Object.keys(deviceCount),
        datasets: [{
          pointBorderColor: "#FFF",
          pointBackgroundColor: "#f96332",
          pointHoverRadius: 4,
          pointHoverBorderWidth: 1,
          pointRadius: 4,
          fill: true,
          backgroundColor:  CHART_COLORS,
          borderWidth: 2,
          data: Object.values(deviceCount)
        }]
      },
      options: gradientChartOptionsConfiguration
    });

   browserrepartitionchart=  new Chart( document.getElementById('browserrepartitionchart').getContext("2d"), {
      type: 'pie',
      responsive: true,
      data: {
        labels: Object.keys(browserCount),
        datasets: [{
          label: "Active Users",
          pointBorderColor: "#FFF",
          pointBackgroundColor: "#f96332",
          pointHoverRadius: 4,
          pointHoverBorderWidth: 1,
          pointRadius: 4,
          fill: true,
          backgroundColor:  CHART_COLORS,
          borderWidth: 2,
          data: Object.values(browserCount)
        }]
      },
      options: gradientChartOptionsConfiguration
    });







	ctx = document.getElementById('hourRepartitionChart').getContext("2d")
    gradientFill = ctx.createLinearGradient(0, 170, 0, 50);
    gradientFill.addColorStop(0, "rgba(128, 182, 244, 0)");
    gradientFill.addColorStop(1, hexToRGB('#18ce0f', 0.4));

  hourRepartitionChart =  new Chart(ctx, {
      type: 'line',
      responsive: true,
      data: {
        labels: Object.keys(hourCount),
        datasets: [{
          label: "Connections by hours",
          borderColor: "#18ce0f",
          pointBorderColor: "#FFF",
          pointBackgroundColor: "#18ce0f",
          pointBorderWidth: 2,
          pointHoverRadius: 4,
          pointHoverBorderWidth: 1,
          pointRadius: 4,
          fill: true,
          backgroundColor: gradientFill,
          borderWidth: 2,
          data: Object.values(hourCount)
        }]
      },
      options: gradientChartOptionsConfigurationWithNumbersAndGrid
    });


    var e = document.getElementById("requestedEndpoint").getContext("2d");
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
            display: 0,
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

   requestedEndpoint =  new Chart(e, a);





	var map = new maplibregl.Map({
	  container: 'connectionMap',
	  style: 'https://maps.geoapify.com/v1/styles/klokantech-basic/style.json?apiKey=a8dc51356cb04465a1c44a8a4c773946',
	});
	map.addControl(new maplibregl.NavigationControl());
	$.each(countriesCount,function(index, value){
		try{
    		new maplibregl.Marker().setLngLat([value.longitude, value.latitude]).addTo(map);
    		}
    		catch(error)
    		{
				console.log(error + " " + JSON.stringify(value));
			}

	});


	




	if(tableEndPoint==null)
	{ 
		tableEndPoint =$('#tableEndpoints').DataTable({
                  'data' : datas,
				  'order': [[ 2, "desc" ]],
				  "responsive": true,
                  'columns' : [
                      {
					   'data' : 'url',
 					   'defaultContent': ""
					},
                      {
					   'data' : 'method',
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
                      {'data' : 'end',
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
                    {
						'data' : 'duration',
						'defaultContent': ""
                    },
                    {
						'data' : 'ip',
						 'defaultContent': ""
					},
                    { 
		                "data": "location",
		                "defaultContent": "",
		                "render": function(d, type, row, meta){
		                	 if(type === 'display'){
								return d.country_name;		                		 
		                	 }
		                   return data;
		                }
		            },
		            { 
		                "data": "userAgent.AgentName",
		                "defaultContent": ""
		            }
                  ]

        });
	}
	else
	{
		tableEndPoint.clear().rows.add(datas).draw();
		
	}
  }
};