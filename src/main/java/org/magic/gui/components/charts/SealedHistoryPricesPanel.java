package org.magic.gui.components.charts;

import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.io.IOException;
import java.util.Date;
import java.util.Map.Entry;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.JFreeChart;
import org.jfree.data.time.Day;
import org.jfree.data.time.TimeSeries;
import org.jfree.data.time.TimeSeriesCollection;
import org.magic.api.beans.HistoryPrice;
import org.magic.api.beans.Packaging;
import org.magic.api.interfaces.MTGDashBoard;
import org.magic.gui.abstracts.MTGUIChartComponent;
import org.magic.services.MTGControler;

public class SealedHistoryPricesPanel extends MTGUIChartComponent<Void> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private transient HistoryPrice<?> cpVariations;
	private String title="";
	private Packaging pack;
	
	
	@Override
	public String getTitle() {
		return "Sealed Price History";
	}
	
	
	public SealedHistoryPricesPanel() {
		
		addComponentListener(new ComponentAdapter() {
			@Override
			public void componentShown(ComponentEvent componentEvent) {
				init(pack,title);
			}
		});
		
	}
	
	public HistoryPrice<?> getVariations() {
		return cpVariations;
	}
	
	public void init(Packaging pack, String title) {
		this.pack=pack;
		this.title = title;
		
		if(isVisible()) {
			try {
				this.cpVariations = MTGControler.getInstance().getEnabled(MTGDashBoard.class).getPriceVariation(pack);
				refresh();
			} catch (IOException e) {
				logger.error("error init " + pack, e);
			}
		}
	}

	@Override
	public JFreeChart initChart() {

		TimeSeriesCollection dataset = new TimeSeriesCollection();

		TimeSeries series1 = new TimeSeries(title);
			if(cpVariations!=null)
				for (Entry<Date, Double> d : cpVariations.entrySet())
					series1.add(new Day(d.getKey()), d.getValue().doubleValue());

			dataset.addSeries(series1);
		
		return ChartFactory.createTimeSeriesChart("Price Variation", "Date", "Value", dataset, true, true,false);
	}


}
