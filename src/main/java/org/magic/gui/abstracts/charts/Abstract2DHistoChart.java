package org.magic.gui.abstracts.charts;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.category.CategoryDataset;
import org.jfree.data.general.Dataset;

public abstract class Abstract2DHistoChart<B> extends MTGUI2DChartComponent<B> {

	private static final long serialVersionUID = 1L;
	
	public boolean viewLabel()
	{
		return true;
	}
	
	public boolean viewLegend()
	{
		return true;
	}
	
	

	@Override
	protected void createNewChart() {
		chart = ChartFactory.createBarChart(getTitle(), "", "", (CategoryDataset)getDataSet(),PlotOrientation.VERTICAL, true, true, false);
	}
	

	public abstract Dataset getDataSet() ;

	

}