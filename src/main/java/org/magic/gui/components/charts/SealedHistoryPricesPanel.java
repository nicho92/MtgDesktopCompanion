package org.magic.gui.components.charts;

import static org.magic.services.tools.MTG.getEnabledPlugin;

import java.util.Date;
import java.util.Map.Entry;

import javax.swing.SwingWorker;

import org.apache.commons.lang3.StringUtils;
import org.jfree.data.time.Day;
import org.jfree.data.time.TimeSeries;
import org.jfree.data.time.TimeSeriesCollection;
import org.magic.api.beans.HistoryPrice;
import org.magic.api.beans.MTGSealedProduct;
import org.magic.api.interfaces.MTGDashBoard;
import org.magic.gui.abstracts.charts.Abstract2DHistoChart;
import org.magic.services.threads.ThreadManager;

public class SealedHistoryPricesPanel extends Abstract2DHistoChart<Void> {

	/**
	 *
	 */
	private static final long serialVersionUID = 1L;
	private transient HistoryPrice<?> cpVariations;
	private String title="";
	private MTGSealedProduct pack;


	@Override
	public String getTitle() {
		return StringUtils.isBlank(title)?"Price History":title;
	}


	@Override
	public void onVisible() {
		init(pack,title);
	}

	public void init(MTGSealedProduct pack, String title) {
		this.pack=pack;
		this.title = title;

		if(isVisible())
		{
				SwingWorker<Void, Void> s=  new SwingWorker<>(){

					@Override
					protected Void doInBackground() throws Exception {
						cpVariations = getEnabledPlugin(MTGDashBoard.class).getPriceVariation(pack);
						return null;
					}

					@Override
					protected void done() {
						refresh();
					}

				};

				ThreadManager.getInstance().runInEdt(s, "loading history price for "+ pack);
		}
	}

	@Override
	public TimeSeriesCollection getDataSet() {
		var dataset = new TimeSeriesCollection();
		var series1 = new TimeSeries(title);
		if(cpVariations!=null)
			for (Entry<Date, Double> d : cpVariations.entrySet())
				series1.add(new Day(d.getKey()), d.getValue().doubleValue());

		dataset.addSeries(series1);
		return dataset;
	}

}
