package org.magic.gui.components.charts;

import java.util.Map.Entry;

import org.jfree.data.category.CategoryDataset;
import org.jfree.data.category.DefaultCategoryDataset;
import org.magic.api.beans.MTGCard;
import org.magic.gui.abstracts.charts.Abstract2DBarChart;

public class CmcChartPanel extends Abstract2DBarChart<MTGCard> {

	private static final long serialVersionUID = 1L;



	@Override
	public CategoryDataset getDataSet() {
		var dataset = new DefaultCategoryDataset();
		for (Entry<Integer, Integer> k : manager.analyseCMC(items).entrySet())
			dataset.addValue(k.getValue(), getTitle(), k.getKey());

		return dataset;
	}

	@Override
	public String getTitle() {
		return "CMC";
	}

}
