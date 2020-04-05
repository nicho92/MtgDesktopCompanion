package org.magic.gui.components.charts;

import java.text.DecimalFormat;
import java.util.Map.Entry;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.labels.PieSectionLabelGenerator;
import org.jfree.chart.labels.StandardPieSectionLabelGenerator;
import org.jfree.chart.plot.PiePlot;
import org.jfree.data.general.DefaultPieDataset;
import org.jfree.data.general.PieDataset;
import org.magic.api.beans.MagicCard;
import org.magic.api.beans.enums.MTGRarity;
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
		
		for(MTGRarity r : MTGRarity.values())
		{
			plot.setSectionPaint(r.toPrettyString(),r.toColor());
		}
		

		PieSectionLabelGenerator generator = new StandardPieSectionLabelGenerator("{0} = {1}", new DecimalFormat("0"),new DecimalFormat("0.00%"));
		plot.setLabelGenerator(generator);
		
		return chart;
	}

	private PieDataset getDataSet() {
		DefaultPieDataset dataset = new DefaultPieDataset();
		for (Entry<MTGRarity, Integer> data : manager.analyseRarities(items).entrySet()) {
			dataset.setValue(data.getKey().toPrettyString(), data.getValue());
		}

		return dataset;
	}

}
