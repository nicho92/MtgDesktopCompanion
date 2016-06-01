package org.magic.gui.components.charts;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;

import javax.swing.JPanel;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.DateAxis;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.data.time.SimpleTimePeriod;
import org.jfree.data.time.TimePeriodValues;
import org.jfree.data.time.TimePeriodValuesCollection;

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
		
		TimePeriodValues  serie = new TimePeriodValues("Date");
		
		for(Date d : map.keySet())
			serie.add(new SimpleTimePeriod(d, d),map.get(d).doubleValue());
		
		
		TimePeriodValuesCollection dataset = new TimePeriodValuesCollection();
		dataset.addSeries(serie);
		
		
		JFreeChart chart = ChartFactory.createHistogram(
                "Price Variation",
                "date",
                "value",
                dataset,
                PlotOrientation.VERTICAL,
                true,
                true,
                false
                );
		
		pane = new ChartPanel(chart);
		
		this.add(pane);
		chart.fireChartChanged();
	}
}
