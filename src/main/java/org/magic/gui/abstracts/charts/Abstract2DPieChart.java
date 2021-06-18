package org.magic.gui.abstracts.charts;

import org.jfree.chart.ChartFactory;
import org.jfree.data.general.PieDataset;

public abstract class Abstract2DPieChart<B> extends MTGUI2DChartComponent<B> {

	private static final long serialVersionUID = 1L;
	
	

	@Override
	protected void createNewChart() {
		chart = ChartFactory.createPieChart(getTitle(), (PieDataset)getDataSet(),showLegend(),true, true);
	}


}