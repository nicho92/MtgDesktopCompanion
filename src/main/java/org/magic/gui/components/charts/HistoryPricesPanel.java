package org.magic.gui.components.charts;

import java.awt.BorderLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
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
import org.magic.api.beans.MagicCard;
import org.magic.api.beans.MagicEdition;
import org.magic.api.interfaces.DashBoard;
import org.magic.services.MTGControler;
import org.magic.services.MTGLogger;

public class HistoryPricesPanel extends JPanel{
	
	boolean showEdition=false;
	boolean showAll=false;
	JCheckBox chckbxShowEditions;
	JCheckBox chckbxShowAllDashboard;
	
	public HistoryPricesPanel() {
		setLayout(new BorderLayout(0, 0));
		JPanel panel = new JPanel();
		add(panel, BorderLayout.EAST);
		pane = new ChartPanel(null);
		GridBagLayout gblpanel = new GridBagLayout();
		gblpanel.columnWidths = new int[]{91, 0};
		gblpanel.rowHeights = new int[]{23, 0, 0};
		gblpanel.columnWeights = new double[]{0.0, Double.MIN_VALUE};
		gblpanel.rowWeights = new double[]{0.0, 0.0, Double.MIN_VALUE};
		panel.setLayout(gblpanel);
		chckbxShowEditions = new JCheckBox("Show Editions");
		chckbxShowEditions.setSelected(showEdition);
		chckbxShowEditions.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				showEdition=chckbxShowEditions.isSelected();
				refresh();
			}
		});
		GridBagConstraints gbcchckbxShowEditions = new GridBagConstraints();
		gbcchckbxShowEditions.anchor = GridBagConstraints.NORTHWEST;
		gbcchckbxShowEditions.insets = new Insets(0, 0, 5, 0);
		gbcchckbxShowEditions.gridx = 0;
		gbcchckbxShowEditions.gridy = 0;
		panel.add(chckbxShowEditions, gbcchckbxShowEditions);
		
		chckbxShowAllDashboard = new JCheckBox("Show all dashboard");
		chckbxShowAllDashboard.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				showAll=chckbxShowAllDashboard.isSelected();
				refresh();
			}
		});
		
		GridBagConstraints gbcchckbxShowAllDashboard = new GridBagConstraints();
		gbcchckbxShowAllDashboard.gridx = 0;
		gbcchckbxShowAllDashboard.gridy = 1;
		panel.add(chckbxShowAllDashboard, gbcchckbxShowAllDashboard);
	}
	
	
	ChartPanel pane;
	private Map<Date, Double> map;
	private String title;
	private MagicCard mc;
	private MagicEdition me;
	
	public void init(MagicCard card, MagicEdition me,String title)
	{
		try {
			this.mc=card;
			this.me=me;
			this.map=MTGControler.getInstance().getEnabledDashBoard().getPriceVariation(card,me);
			this.title=title;
			refresh();
		} catch (IOException e) {
			MTGLogger.printStackTrace(e);
		}
		
	}
	
	private void refresh()
	{

		TimeSeriesCollection dataset = new TimeSeriesCollection();
		
		TimeSeries series1 = new TimeSeries(title);
		if(showAll)
		{
			for(DashBoard d : MTGControler.getInstance().getDashBoards())
			{
				TimeSeries series = new TimeSeries(d.getName());
				Map<Date, Double> mapTime;
				try {
					mapTime = d.getPriceVariation(mc, me);
					if(mapTime!=null)
					{
						for(Date da : mapTime.keySet())
							series.add(new Day(da),mapTime.get(da).doubleValue());
					
					dataset.addSeries(series);
					}
					
				} catch (IOException e) {
					MTGLogger.printStackTrace(e);
				}
				
			}
				
		}
		else
		{
			
			for(Date d : map.keySet())
				series1.add(new Day(d),map.get(d).doubleValue());
			
			dataset.addSeries(series1);
		}
			
		
		
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
				for(MagicEdition edition: MTGControler.getInstance().getEnabledProviders().loadEditions())
					{
						Date d = new SimpleDateFormat("yyyy-MM-dd hh:mm").parse(edition.getReleaseDate()+" 00:00");
						TimeSeriesDataItem  item = series1.getDataItem(new Day(d));
						
						if(item!=null)
						{ 
							
						  double x = item.getPeriod().getFirstMillisecond();
						  double y = item.getValue().doubleValue();
						  XYTextAnnotation  annot = new XYTextAnnotation (edition.getId(),x,y);
						  					annot.setToolTipText(edition.getSet());
						  XYPlot plot = (XYPlot) chart.getPlot();
						  plot.addAnnotation(annot);
						}
					}
				}
			catch(Exception e)
			{
				MTGLogger.printStackTrace(e);
			}
			
		
		
		pane.setChart(chart);
		pane.addMouseWheelListener(mwe->{
	            if (mwe.getWheelRotation() > 0) {
	            	pane.zoomOutDomain(0.5, 0.5);
	            	
	            } else if (mwe.getWheelRotation() < 0) {
	            	pane.zoomInDomain(1.5, 1.5);
	            }
	    });
		this.add(pane,BorderLayout.CENTER);
		chart.fireChartChanged();
	}
	
	public void zoom()
	{
		 //do nothing
	}

}
