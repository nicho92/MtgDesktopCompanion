var bigDashboard;
var tableQueriesCalls;

const chartColor = "#FFFFFF";
		
server = {
  
    initDashboardPageCharts: function(data, start, end) {
		var datas = data.jobs.filter(a => {
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
	
	if(bigDashboard!=null){
		bigDashboard.destroy();
	}
	
	
	
	
	if(tableQueriesCalls==null)
	{
		tableQueriesCalls =$('#tableQueriesCalls').DataTable({
                  'data' : datas,
				  'responsive': true,
			  	  'order': [[ 1, "desc" ]],
		
                "columns": [
 		        	{ 
		                "data": "jobName",
		                "defaultContent": ""
		            },
		           { 
		                "data": "start",
		                "defaultContent": "",
		                "render": function(data, type, row, meta){
		                	 if(type === 'display'){
									return new Date(data).toLocaleString();		                		 
		                	 }
		                   return data;
		                }
		            },
		            { 
		                "data": "duration",
		                "defaultContent": ""
		            }
		            
		            ]

        });
	}
	else
	{
		queriesCalls.clear().rows.add(data).draw();
	}
	
	
  }
};