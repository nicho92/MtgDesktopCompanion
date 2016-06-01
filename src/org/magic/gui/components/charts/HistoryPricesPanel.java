package org.magic.gui.components.charts;

import java.util.Date;
import java.util.Map;

import javax.swing.JPanel;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.data.time.Day;
import org.jfree.data.time.TimeSeries;
import org.jfree.data.time.TimeSeriesCollection;

public class HistoryPricesPanel extends JPanel{
	ChartPanel pane;
	private Map<Date, Double> map;
	
	public void init(Map<Date,Double> map)
	{
		this.map=map;
		refresh();
	}
	
	
	private void refresh()
	{
		this.removeAll();
		
		  TimeSeries series1 = new TimeSeries("value");
			for(Date d : map.keySet())
				series1.add(new Day(d),map.get(d).doubleValue());

		  TimeSeriesCollection dataset = new TimeSeriesCollection();
		    dataset.addSeries(series1);
		
		
		
		JFreeChart chart = ChartFactory.createTimeSeriesChart(
                "Price Variation",
                "date",
                "Price",
                dataset,
                true,
                true,
                false
                );
		
		pane = new ChartPanel(chart);
		
		this.add(pane);
		chart.fireChartChanged();
	}
}
