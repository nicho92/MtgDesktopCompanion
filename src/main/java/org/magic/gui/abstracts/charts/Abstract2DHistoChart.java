package org.magic.gui.abstracts.charts;

import org.jfree.chart.ChartFactory;
import org.jfree.data.time.TimeSeriesCollection;

public abstract class Abstract2DHistoChart<B> extends MTGUI2DChartComponent<B,TimeSeriesCollection> {

	private static final long serialVersionUID = 1L;


	@Override
	protected void createNewChart() {
		chart = ChartFactory.createTimeSeriesChart(getTitle(), "Date", "Value", getDataSet(),showLegend(), true, false);
	}

}