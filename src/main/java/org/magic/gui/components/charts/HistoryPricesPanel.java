package org.magic.gui.components.charts;

import static org.magic.tools.MTG.getEnabledPlugin;

import java.awt.BorderLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map.Entry;

import javax.swing.ImageIcon;
import javax.swing.JCheckBox;
import javax.swing.JPanel;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.annotations.XYImageAnnotation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.data.time.Day;
import org.jfree.data.time.TimeSeries;
import org.jfree.data.time.TimeSeriesCollection;
import org.jfree.data.time.TimeSeriesDataItem;
import org.magic.api.beans.HistoryPrice;
import org.magic.api.beans.MagicCard;
import org.magic.api.beans.MagicEdition;
import org.magic.api.interfaces.MTGCardsProvider;
import org.magic.api.interfaces.MTGDashBoard;
import org.magic.gui.abstracts.MTGUIChartComponent;
import org.magic.services.MTGConstants;
import org.magic.services.extra.IconSetProvider;
import org.magic.tools.UITools;

public class HistoryPricesPanel extends MTGUIChartComponent<Void> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	boolean showEdition = false;
	private JCheckBox chckbxShowEditions;
	private transient HistoryPrice<?> cpVariations;
	private String title="";
	private MagicCard mc;
	private MagicEdition me;
	private transient HistoryPrice<?> cpVariationsF;
	
	@Override
	public ImageIcon getIcon() {
		return MTGConstants.ICON_TAB_VARIATIONS;
	}
	
	@Override
	public String getTitle() {
		return "Price History Chart";
	}
	
	
	public HistoryPricesPanel(boolean showOption) {
		
		if(showOption) {
		
		JPanel panelActions = new JPanel();
		add(panelActions, BorderLayout.EAST);
		
		GridBagLayout gblpanel = new GridBagLayout();
		gblpanel.columnWidths = new int[] { 91, 0 };
		gblpanel.rowHeights = new int[] { 23, 0, 0 };
		gblpanel.columnWeights = new double[] { 0.0, Double.MIN_VALUE };
		gblpanel.rowWeights = new double[] { 0.0, 0.0, Double.MIN_VALUE };
		panelActions.setLayout(gblpanel);
		chckbxShowEditions = new JCheckBox("Show Editions");
		chckbxShowEditions.setSelected(showEdition);
		chckbxShowEditions.addActionListener(ae -> {
			showEdition = chckbxShowEditions.isSelected();
			refresh();
		});
		panelActions.add(chckbxShowEditions, UITools.createGridBagConstraints(GridBagConstraints.NORTHWEST, null, 0, 0));

		
		
		}
		
		addComponentListener(new ComponentAdapter() {
			@Override
			public void componentShown(ComponentEvent componentEvent) {
				init(mc,me,title);
			}
		});
		
	}

	
	public void init(MagicCard card, MagicEdition me, String title) {
		this.mc = card;
		this.me = me;
		
		if(card==null && me==null)
			return;
		
		if(me==null)
			me=card.getCurrentSet();
		
		this.title = title;
		
		if(isVisible()) 
		{
			try {
				this.cpVariations = getEnabledPlugin(MTGDashBoard.class).getPriceVariation(card, me,false);
				
			} catch (IOException e) {
				logger.error("error init " + card, e);
			}
			
			try {
				this.cpVariationsF = getEnabledPlugin(MTGDashBoard.class).getPriceVariation(card, me,true);
				
			} catch (IOException e) {
				logger.error("error init FOIL " + card, e);
			}
			refresh();
		}
	}

	@Override
	public JFreeChart initChart() {

		TimeSeriesCollection dataset = new TimeSeriesCollection();

		TimeSeries series1=null;
		
			if(cpVariations!=null && !cpVariations.isEmpty())
			{	
				
				series1 = new TimeSeries(cpVariations.toString());
				
				for (Entry<Date, Double> d : cpVariations.entrySet())
					series1.add(new Day(d.getKey()), d.getValue().doubleValue());

				dataset.addSeries(series1);
			}
			if(cpVariationsF!=null && !cpVariationsF.isEmpty())
			{
				TimeSeries series2 = new TimeSeries(cpVariationsF.toString());

				for (Entry<Date, Double> d : cpVariationsF.entrySet())
					series2.add(new Day(d.getKey()), d.getValue().doubleValue());
				
				dataset.addSeries(series2);
			}
			
		JFreeChart chart = ChartFactory.createTimeSeriesChart("Price Variation", "Date", "Value", dataset, true, true,false);
		
		
		if(series1==null)
			return chart;
		

		if (showEdition)
		{		
			
			List<MagicEdition> list = new ArrayList<>();
			try {
				list = getEnabledPlugin(MTGCardsProvider.class).listEditions();
			} catch (IOException e1) {
				logger.error(e1);
			}
			
				for (MagicEdition edition : list) 
				{
					try {	
					Date d = new SimpleDateFormat("yyyy-MM-dd hh:mm").parse(edition.getReleaseDate() + " 00:00");
					TimeSeriesDataItem item = series1.getDataItem(new Day(d));

					if (item != null) {

						double x = item.getPeriod().getFirstMillisecond();
						double y = item.getValue().doubleValue();
						XYImageAnnotation annot = new XYImageAnnotation(x,y,IconSetProvider.getInstance().get16(edition.getId()).getImage()); 
										  annot.setToolTipText(edition.getSet());
						XYPlot plot = (XYPlot) chart.getPlot();
						plot.addAnnotation(annot);
					}
				
			} catch (Exception e) {
				logger.error("error show eds " + edition+ " :" + e);
			}
				}
		}
		return chart;
	}


}
