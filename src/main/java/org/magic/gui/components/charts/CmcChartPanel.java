package org.magic.gui.components.charts;

import java.util.Map.Entry;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.category.CategoryDataset;
import org.jfree.data.category.DefaultCategoryDataset;
import org.magic.api.beans.MagicCard;
import org.magic.gui.abstracts.MTGUI2DChartComponent;

public class CmcChartPanel extends MTGUI2DChartComponent<MagicCard> {
	
	private static final long serialVersionUID = 1L;

	@Override
	public JFreeChart initChart() {
		return ChartFactory.createBarChart("Mana Curve", "cost", "number", getDataSet(),PlotOrientation.VERTICAL, true, true, false);
	}


	private CategoryDataset getDataSet() {
		var dataset = new DefaultCategoryDataset();
		for (Entry<Integer, Integer> k : manager.analyseCMC(items).entrySet())
			dataset.addValue(k.getValue(), "cmc", k.getKey());

		return dataset;
	}

	@Override
	public String getTitle() {
		return "CMC Chart";
	}

}
