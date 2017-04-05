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
import org.magic.services.MTGControler;
import javax.swing.JButton;
import java.awt.GridBagLayout;
import java.awt.GridBagConstraints;
import java.awt.Insets;

public class HistoryPricesPanel extends JPanel{
	
	boolean showEdition=false;
	JCheckBox chckbxShowEditions;
	
	
	public HistoryPricesPanel() {
		setLayout(new BorderLayout(0, 0));
		JPanel panel = new JPanel();
		add(panel, BorderLayout.EAST);
		pane = new ChartPanel(null);
		GridBagLayout gbl_panel = new GridBagLayout();
		gbl_panel.columnWidths = new int[]{91, 0};
		gbl_panel.rowHeights = new int[]{23, 0, 0};
		gbl_panel.columnWeights = new double[]{0.0, Double.MIN_VALUE};
		gbl_panel.rowWeights = new double[]{0.0, 0.0, Double.MIN_VALUE};
		panel.setLayout(gbl_panel);
		chckbxShowEditions = new JCheckBox("Show Editions");
		chckbxShowEditions.setSelected(showEdition);
		chckbxShowEditions.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				showEdition=chckbxShowEditions.isSelected();
				refresh();
			}
		});
		GridBagConstraints gbc_chckbxShowEditions = new GridBagConstraints();
		gbc_chckbxShowEditions.anchor = GridBagConstraints.NORTHWEST;
		gbc_chckbxShowEditions.insets = new Insets(0, 0, 5, 0);
		gbc_chckbxShowEditions.gridx = 0;
		gbc_chckbxShowEditions.gridy = 0;
		panel.add(chckbxShowEditions, gbc_chckbxShowEditions);
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
				for(MagicEdition me: MTGControler.getInstance().getEnabledProviders().loadEditions())
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
