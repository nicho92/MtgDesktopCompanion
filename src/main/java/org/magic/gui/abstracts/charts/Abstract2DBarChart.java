package org.magic.gui.abstracts.charts;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.category.CategoryDataset;

public abstract class Abstract2DBarChart<B> extends MTGUI2DChartComponent<B,CategoryDataset> {

	private static final long serialVersionUID = 1L;


	@Override
	protected void createNewChart() {
		chart = ChartFactory.createBarChart(getTitle(), "", "", getDataSet(),PlotOrientation.VERTICAL, showLegend(), true, false);
	}

}