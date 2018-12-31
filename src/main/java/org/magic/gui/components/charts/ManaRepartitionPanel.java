package org.magic.gui.components.charts;

import java.awt.Color;
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
import org.magic.gui.abstracts.MTGUIChartComponent;

public class ManaRepartitionPanel extends MTGUIChartComponent<MagicCard> {

	private static final long serialVersionUID = 1L;
	
	
	@Override
	public String getTitle() {
		return "Mana Chart";
	}

	@Override
	public JFreeChart initChart() {
		
		JFreeChart chart = ChartFactory.createPieChart3D("Color repartition", // chart title
				getDataSet(), // data
				false, // include legend
				true, true);
		
		PiePlot plot = (PiePlot) chart.getPlot();
		plot.setSectionPaint("Black", Color.BLACK);
		plot.setSectionPaint("White", Color.WHITE);
		plot.setSectionPaint("Blue", Color.BLUE);
		plot.setSectionPaint("Green", Color.GREEN);
		plot.setSectionPaint("Red", Color.RED);
		plot.setSectionPaint("Multi", Color.YELLOW);
		plot.setSectionPaint("Uncolor", Color.GRAY);
		plot.setSectionPaint("black", Color.BLACK);
		plot.setSectionPaint("white", Color.WHITE);
		plot.setSectionPaint("blue", Color.BLUE);
		plot.setSectionPaint("green", Color.GREEN);
		plot.setSectionPaint("red", Color.RED);
		plot.setSectionPaint("multi", Color.YELLOW);
		plot.setSectionPaint("uncolor", Color.GRAY);
		plot.setSimpleLabels(true);

		PieSectionLabelGenerator generator = new StandardPieSectionLabelGenerator("{1}", new DecimalFormat("0"),
				new DecimalFormat("0.00%"));
		plot.setLabelGenerator(generator);

		return chart;
	}

	private PieDataset getDataSet() {
		DefaultPieDataset dataset = new DefaultPieDataset();
		for (Entry<String, Integer> data : manager.analyseColors(items).entrySet()) {
			dataset.setValue(data.getKey(), data.getValue());

		}

		return dataset;
	}

}
