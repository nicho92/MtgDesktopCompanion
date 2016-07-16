package org.magic.gui.components.charts;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;

import javax.swing.JCheckBox;
import javax.swing.JPanel;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.annotations.XYTextAnnotation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.data.time.Day;
import org.jfree.data.time.TimeSeries;
import org.jfree.data.time.TimeSeriesCollection;
import org.jfree.data.time.TimeSeriesDataItem;
import org.magic.api.beans.MagicEdition;
import org.magic.services.MagicFactory;

public class HistoryPricesPanel extends JPanel{
	
	boolean showEdition=true;
	JCheckBox chckbxShowEditions;
	
	
	public HistoryPricesPanel() {
		setLayout(new BorderLayout(0, 0));
		JPanel panel = new JPanel();
		add(panel, BorderLayout.EAST);
		pane = new ChartPanel(null);
		chckbxShowEditions = new JCheckBox("Show Editions");
		chckbxShowEditions.setSelected(showEdition);
		chckbxShowEditions.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				showEdition=chckbxShowEditions.isSelected();
				refresh();
			}
		});
		panel.add(chckbxShowEditions);
	}
	
	
	ChartPanel pane;
	private Map<Date, Double> map;
	private String mc;
	
	public void init(Map<Date,Double> map,String title)
	{
		this.map=map;
		this.mc=title;
		refresh();
	}
	
	private void refresh()
	{

		TimeSeriesCollection dataset = new TimeSeriesCollection();
		TimeSeries series1 = new TimeSeries(mc);
			for(Date d : map.keySet())
				series1.add(new Day(d),map.get(d).doubleValue());

		
		dataset.addSeries(series1);
		JFreeChart chart = ChartFactory.createTimeSeriesChart(
	                "Price Variation",
	                "Date",
	                "Price",
	                dataset,
	                true,
	                true,
	                false
	                );	
			
			
		if(showEdition)	
		try{
				for(MagicEdition me: MagicFactory.getInstance().getEnabledProviders().searchSetByCriteria(null, null))
					{
						Date d = new SimpleDateFormat("yyyy-MM-dd hh:mm").parse(me.getReleaseDate()+" 00:00");
						TimeSeriesDataItem  item = series1.getDataItem(new Day(d));
						
						if(item!=null)
						{ 
							
						  double x = item.getPeriod().getFirstMillisecond();
						  double y = item.getValue().doubleValue();
						  XYTextAnnotation  annot = new XYTextAnnotation (me.getId(),x,y);
						  					annot.setToolTipText(me.getSet());
						  XYPlot plot = (XYPlot) chart.getPlot();
						  plot.addAnnotation(annot);
						}
					}
				}
			catch(Exception e)
			{
				
			}
			
		
		
		pane.setChart(chart);
		pane.addMouseWheelListener(new MouseWheelListener() {
	        public void mouseWheelMoved(MouseWheelEvent arg0) {
	            if (arg0.getWheelRotation() > 0) {
	            	pane.zoomOutDomain(0.5, 0.5);
	            } else if (arg0.getWheelRotation() < 0) {
	            	pane.zoomInDomain(1.5, 1.5);
	            }
	        }
	    });
		this.add(pane,BorderLayout.CENTER);
		chart.fireChartChanged();
	}
}
