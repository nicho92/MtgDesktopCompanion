package org.magic.gui.components.charts;

import java.awt.BorderLayout;
import java.awt.Color;
import java.text.DecimalFormat;
import java.util.Map.Entry;

import org.apache.commons.lang3.StringUtils;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.labels.PieSectionLabelGenerator;
import org.jfree.chart.labels.StandardPieSectionLabelGenerator;
import org.jfree.chart.plot.PiePlot;
import org.jfree.data.general.DefaultPieDataset;
import org.jfree.data.general.PieDataset;
import org.magic.api.beans.MagicCard;
import org.magic.gui.abstracts.MTGUIChartComponent;

public class RarityRepartitionPanel extends MTGUIChartComponent<MagicCard> {

	private static final long serialVersionUID = 1L;

	
	@Override
	public String getTitle() {
		return "Rarity Chart";
	}
	
	@Override
	public JFreeChart initChart() {
		JFreeChart chart = ChartFactory.createPieChart3D("Rarity repartition", getDataSet(), false, true, true);
		PiePlot plot = (PiePlot) chart.getPlot();
		plot.setSectionPaint("Uncommon", Color.GRAY);
		plot.setSectionPaint("Common", Color.WHITE);
		plot.setSectionPaint("Rare", Color.YELLOW);
		plot.setSectionPaint("Mythic", Color.ORANGE);

		PieSectionLabelGenerator generator = new StandardPieSectionLabelGenerator("{0} = {1}", new DecimalFormat("0"),new DecimalFormat("0.00%"));
		plot.setLabelGenerator(generator);
		
		return chart;
	}

	private PieDataset getDataSet() {
		DefaultPieDataset dataset = new DefaultPieDataset();
		for (Entry<String, Integer> data : manager.analyseRarities(items).entrySet()) {
			dataset.setValue(StringUtils.capitalize(data.getKey()), data.getValue());
		}

		return dataset;
	}

}
