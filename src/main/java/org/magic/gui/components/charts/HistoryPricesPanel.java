package org.magic.gui.components.charts;

import static org.magic.tools.MTG.getEnabledPlugin;

import java.awt.BorderLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map.Entry;

import javax.swing.ImageIcon;
import javax.swing.JCheckBox;
import javax.swing.JPanel;
import javax.swing.SwingWorker;

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
import org.magic.gui.abstracts.AbstractBuzyIndicatorComponent;
import org.magic.gui.abstracts.charts.Abstract2DHistoChart;
import org.magic.services.MTGConstants;
import org.magic.services.providers.IconSetProvider;
import org.magic.services.threads.ThreadManager;
import org.magic.tools.UITools;

public class HistoryPricesPanel extends Abstract2DHistoChart<Void> {

	
	private static final long serialVersionUID = 1L;
	boolean showEdition = false;
	private JCheckBox chckbxShowEditions;
	private transient HistoryPrice<?> cpVariations;
	private String title="";
	private transient HistoryPrice<?> cpVariationsF;
	private MagicCard mc;
	private MagicEdition me;
	private AbstractBuzyIndicatorComponent buzy;
	private TimeSeries series1;
	
	@Override
	public ImageIcon getIcon() {
		return MTGConstants.ICON_TAB_VARIATIONS;
	}
	
	@Override
	public String getTitle() {
		return title;
	}
	
	
	public HistoryPricesPanel(boolean showOption) {
		
		buzy = AbstractBuzyIndicatorComponent.createLabelComponent();
		if(showOption) {
			var panelActions = new JPanel();
			var gblpanel = new GridBagLayout();
			
			add(panelActions, BorderLayout.EAST);
			add(buzy,BorderLayout.SOUTH);

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
	}
	
	@Override
	public void onVisible() {
		init(mc,me,title);
	}
	
	public void init(MagicCard card, MagicEdition me, String title) {
		this.mc = card;
		this.me = me;
		this.title=title;
		
		
		if(card==null && me==null)
			return;
	
		
		if(isVisible()) 
		{
			buzy.start();
			SwingWorker<Void, Void> sw=  new SwingWorker<>(){

				@Override
				protected Void doInBackground() throws Exception {
					if(card==null)
					{
						try {
							cpVariations = getEnabledPlugin(MTGDashBoard.class).getPriceVariation(me);
						} catch (IOException e1) {
							logger.error("error init " + me, e1);
						}
					}
					else
					{
						try {
							cpVariations = getEnabledPlugin(MTGDashBoard.class).getPriceVariation(card, false);
							
						} catch (IOException e) {
							logger.error("error init " + card, e);
						}
						
						try {
							cpVariationsF = getEnabledPlugin(MTGDashBoard.class).getPriceVariation(card, true);
							
						} catch (IOException e) {
							logger.error("error init FOIL " + card, e);
						}
					}
					return null;
				}
				
				@Override
				protected void done() {
					buzy.end();
					refresh();
				}
				
			};
			
			ThreadManager.getInstance().runInEdt(sw, "loading history "+ mc);
		}
	}

	
	@Override
	public TimeSeriesCollection getDataSet() {
		var dataset = new TimeSeriesCollection();

			series1=null;
		
			if(cpVariations!=null && !cpVariations.isEmpty())
			{	
				
				series1 = new TimeSeries(cpVariations.toString());
				
				for (Entry<Date, Double> d : cpVariations.entrySet())
					series1.add(new Day(d.getKey()), UITools.roundDouble(d.getValue().doubleValue()));

				dataset.addSeries(series1);
			}
			
			if(cpVariationsF!=null && !cpVariationsF.isEmpty())
			{
				var series2 = new TimeSeries(cpVariationsF.toString());

				for (Entry<Date, Double> d : cpVariationsF.entrySet())
					series2.add(new Day(d.getKey()), UITools.roundDouble(d.getValue().doubleValue()));
				
				dataset.addSeries(series2);
			}
		
			
			return dataset;
	}
	
	@Override
	protected void initPlot() {
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
				if(edition.getReleaseDate()!=null && !edition.getReleaseDate().isEmpty()) 
				{
					try {	
						Date d = new SimpleDateFormat("yyyy-MM-dd hh:mm").parse(edition.getReleaseDate() + " 00:00");
						TimeSeriesDataItem item = series1.getDataItem(new Day(d));
						if (item != null) 
						{
							var x = item.getPeriod().getFirstMillisecond();
							var y = item.getValue().doubleValue();
							var annot = new XYImageAnnotation(x,y,IconSetProvider.getInstance().get16(edition.getId()).getImage()); 
								annot.setToolTipText(edition.getSet());
								
							 ((XYPlot) chart.getPlot()).addAnnotation(annot);
						}
				
					} catch (Exception e) {
						logger.error("error show eds " + edition+ " :" + e);
					}
				}
			}
		}
	}
	

}
