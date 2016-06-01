package org.magic.gui.components.charts;

import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;

import javax.swing.JPanel;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.data.time.Day;
import org.jfree.data.time.TimeSeries;
import org.jfree.data.time.TimeSeriesCollection;
import org.jfree.data.xy.XYSeries;
import org.magic.api.beans.MagicEdition;
import org.magic.tools.MagicFactory;

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
		pane.addMouseWheelListener(new MouseWheelListener() {
	        public void mouseWheelMoved(MouseWheelEvent arg0) {
	            if (arg0.getWheelRotation() > 0) {
	            	pane.zoomOutDomain(0.5, 0.5);
	            } else if (arg0.getWheelRotation() < 0) {
	            	pane.zoomInDomain(1.5, 1.5);
	            }
	        }
	    });
		this.add(pane);
		chart.fireChartChanged();
	}
}
