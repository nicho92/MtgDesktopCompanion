charts = {
 
 copyToClipboard: function(element) {
  var $temp = $("<input>");
  $("body").append($temp);
  $temp.val($(element).text()).select();
  document.execCommand("copy");
  $temp.remove();
  },
 
 
 
  initCharts: function(datas) {
    chartColor = "#FFFFFF";

    
    var speedCanvas = document.getElementById("speedChart");
	
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
      labels: Object.values(datas.normal).map(e=>moment(e.date).format("MM/YYYY")),
      datasets: [dataNormal, dataFoil]
    };

    var chartOptions = {
      legend: {
        display: true,
        position: 'bottom'
      }
    };

    new Chart(speedCanvas, {
      type: 'line',
      hover: false,
      data: dateLabels,
      options: chartOptions
    });



  }

};