var chart;


charts = {

  initCharts: function(datas) {
	
	if(chart!=null)
		chart.destroy();
	
    chartColor = "#FFFFFF";

    var chartCanvas = document.getElementById("chart");

	
    var dataNormal = {
      data: Object.values(datas.normal).map(e=>e.value),
      fill: false,
      borderColor: '#fbc658',
      backgroundColor: 'transparent',
      pointBorderColor: '#fbc658',
      pointRadius: 1,
      pointHoverRadius: 1,
      pointBorderWidth: 1,
 	  label:"Normal"
    };

    var dataFoil= {
      data: Object.values(datas.foil).map(e=>e.value),
      fill: false,
      borderColor: '#51CACF',
      backgroundColor: 'transparent',
      pointBorderColor: '#51CACF',
      pointRadius: 1,
      pointHoverRadius: 1,
      pointBorderWidth: 1,
	  label:"Foil"
    };

    var dateLabels = {
      labels: Object.values(datas.normal).map(e=>moment(e.date)),
      datasets: [dataNormal, dataFoil]
    };
    
	const zoomOptions = {
	  pan: {
	    enabled: true,
	    modifierKey: 'ctrl',
	  },
	  zoom: {
	    drag: {
	      enabled: true,
	    },
	    mode: 'xy',
	  },
	};


    var chartOptions = {
      legend: {
        display: true,
        position: 'bottom'
      },
	scales: {
      xAxes: [{
        type: 'time'
      }]
    },
 plugins: {
      zoom: zoomOptions,
      title: {
        display: true,
        position: 'bottom',
        text: (ctx) => 'Zoom: ' + zoomStatus() + ', Pan: ' + panStatus()
      }
    }
    };
	
	
	
	
	
   chart= new Chart(chartCanvas, {
      type: 'line',
      hover: false,
      data: dateLabels,
      options: chartOptions
    });



  }

};