package org.magic.gui.components.charts;

import java.awt.BorderLayout;
import java.util.Map;
import java.util.Map.Entry;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.category.CategoryDataset;
import org.jfree.data.category.DefaultCategoryDataset;
import org.magic.api.beans.MagicCard;
import org.magic.gui.abstracts.MTGUIChartComponent;

public class CmcChartPanel extends MTGUIChartComponent<MagicCard> {
	
	private static final long serialVersionUID = 1L;

	@Override
	public void drawGraph() {
		chart = ChartFactory.createBarChart("Mana Curve", "cost", "number", getDataSet(),PlotOrientation.VERTICAL, true, true, false);
		chartPanel = new ChartPanel(chart,true);
		add(chartPanel, BorderLayout.CENTER);
		chart.fireChartChanged();
		chartPanel.revalidate();
	}


	private CategoryDataset getDataSet() {
		DefaultCategoryDataset dataset = new DefaultCategoryDataset();
		Map<Integer, Integer> temp = manager.analyseCMC(items);
		for (Entry<Integer, Integer> k : temp.entrySet())
			dataset.addValue(k.getValue(), "cmc", k.getKey());

		return dataset;
	}

	@Override
	public String getTitle() {
		return "CMC Chart";
	}

}
