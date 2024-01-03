package org.magic.gui.abstracts.charts;

import java.text.DecimalFormat;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.data.time.TimeSeriesCollection;

public abstract class Abstract2DHistoChart<B> extends MTGUI2DChartComponent<B,TimeSeriesCollection> {

	private static final long serialVersionUID = 1L;


	@Override
	protected void createNewChart() {
		chart = ChartFactory.createTimeSeriesChart(getTitle(), "Date", "Value", getDataSet(),showLegend(), true, false);
		
		var formatter = DecimalFormat.getInstance();
		formatter.setMinimumFractionDigits(2);
		
		((NumberAxis)chart.getXYPlot().getRangeAxis()).setNumberFormatOverride(formatter);
	}

}
