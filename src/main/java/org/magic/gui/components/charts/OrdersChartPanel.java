package org.magic.gui.components.charts;

import java.lang.reflect.InvocationTargetException;
import java.text.DecimalFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.beanutils.PropertyUtils;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.labels.PieSectionLabelGenerator;
import org.jfree.chart.labels.StandardPieSectionLabelGenerator;
import org.jfree.chart.plot.PiePlot;
import org.jfree.data.general.DefaultPieDataset;
import org.jfree.data.general.PieDataset;
import org.jfree.data.time.Day;
import org.jfree.data.time.TimeSeries;
import org.jfree.data.time.TimeSeriesCollection;
import org.magic.api.beans.OrderEntry;
import org.magic.gui.abstracts.MTGUIChartComponent;
import org.magic.tools.UITools;


public class OrdersChartPanel extends MTGUIChartComponent<OrderEntry> {

	private static final long serialVersionUID = 1L;
	private String p;
	private boolean count;


	@Override
	public JFreeChart initChart() {
		
		JFreeChart chart=null ;
		try {
			if(PropertyUtils.getProperty(new OrderEntry(), p) instanceof Date)
			{
				chart = ChartFactory.createTimeSeriesChart("Orders", "Date", "Value", getTimeDataSet(), true, true,false);

			}
			else
			{
				chart = ChartFactory.createPieChart3D("Orders", getPieDataSet(), false, true, true);
				PiePlot plot = (PiePlot) chart.getPlot();
				PieSectionLabelGenerator generator = new StandardPieSectionLabelGenerator("{0} = {1}", new DecimalFormat("0"),new DecimalFormat("0.00%"));
				plot.setLabelGenerator(generator);
					
			}
		} catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
			logger.error(e);
		}
		return chart;
	}


	private TimeSeriesCollection getTimeDataSet() {
		TimeSeriesCollection col = new TimeSeriesCollection();
		
		TimeSeries series1 = new TimeSeries("");
		groupOrdersBy().entrySet().forEach(d->series1.add(new Day((Date)d.getKey()), d.getValue().doubleValue()));
		
		col.addSeries(series1);
		
		return col;
	}


	private PieDataset<String> getPieDataSet() {
		DefaultPieDataset<String> dataset = new DefaultPieDataset<>();
		for (Entry<Object, Double> data : groupOrdersBy().entrySet()) {
			dataset.setValue(String.valueOf(data.getKey()), data.getValue());
		}

		return dataset;
	}
	

	@Override
	public String getTitle() {
		return "Orders Chart";
	}


	public void init(List<OrderEntry> listOrders, String p, boolean count) {
		this.p=p;
		this.count=count;
		init(listOrders);
		
	}
	
	private Map<Object, Double> groupOrdersBy() {
		
		Map<Object, Double> ret = new HashMap<>();
		
		items.forEach(o->{
			try 
			{
				Object val = PropertyUtils.getProperty(o, p);
				if(count)
					ret.put(val, ret.get(val)==null? 1 : ret.get(val)+1);
				else
				{
					ret.put(val, ret.get(val)==null? o.getItemPrice() : UITools.roundDouble(ret.get(val)+o.getItemPrice()));
				}

			} catch (Exception e) {
				logger.error(e);
			} 
			
		});
		return ret;
	}
	

}
