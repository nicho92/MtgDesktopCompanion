jsonserver = {
  
    initDashboardPageCharts: function(datas) {
	
	const CHART_COLORS = [
	  'rgb(255, 99, 132)',
	  'rgb(255, 159, 64)',
	  'rgb(255, 205, 86)',
	  'rgb(75, 192, 192)',
	  'rgb(54, 162, 235)',
	  'rgb(153, 102, 255)',
	  'rgb(201, 203, 207)'
	];
	
	
	
	
		var dayCounts = datas.reduce(function (result, d) {
								    var day = moment(d.start).format("YYYY-MM-DD HH");
								    if (!result[day]) {
								        result[day] = 0;
								    }
								    result[day]++;
								    return result;
									}, {});
	
	
		var deviceCount = datas.reduce(function (result, d) {
								    var device = d.userAgent.OperatingSystemName
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
								    result[uri] = d.duration;
								    console.log(result);
								    return result;
									}, {});
	
	
	
    var chartColor = "#FFFFFF";

    // General configuration for the charts with Line gradientStroke
    gradientChartOptionsConfiguration = {
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

    gradientChartOptionsConfigurationWithNumbersAndGrid = {
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

    var ctx = document.getElementById('bigDashboardChart').getContext("2d");

    var gradientStroke = ctx.createLinearGradient(500, 0, 100, 0);
    gradientStroke.addColorStop(0, '#80b6f4');
    gradientStroke.addColorStop(1, chartColor);

    var gradientFill = ctx.createLinearGradient(0, 200, 0, 50);
    gradientFill.addColorStop(0, "rgba(128, 182, 244, 0)");
    gradientFill.addColorStop(1, "rgba(255, 255, 255, 0.24)");

    new Chart(ctx, {
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

	
    new Chart( document.getElementById('deviceRepartitionChart').getContext("2d"), {
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

    new Chart( document.getElementById('browserrepartitionchart').getContext("2d"), {
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

    new Chart(ctx, {
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

    new Chart(e, a);

$('#tableEndpoints').DataTable({
                  'data' : datas,
				  'order': [[ 2, "desc" ]],
				  "responsive": true,
                  'columns' : [
                      {'data' : 'url'},
                      {'data' : 'method'},
                      {
                          'data' : 'start',
                          render : function(d,type,row){
                              return moment(d).format('DD MMM YYYY, HH:mm:ss');
                          }
                        },
                      {'data' : 'end',
                      render : function(d,type,row){
                               return moment(d).format('DD MMM YYYY, HH:mm:ss');
                      }
                    },
                      {'data' : 'duration'},
                      {'data' : 'ip'}
                  ]

        });
	



    }
};