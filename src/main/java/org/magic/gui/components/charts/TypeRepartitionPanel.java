package org.magic.gui.components.charts;

import java.awt.BorderLayout;
import java.awt.Color;
import java.text.DecimalFormat;
import java.util.Map.Entry;

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

public class TypeRepartitionPanel extends  MTGUIChartComponent<MagicCard> {

	private static final long serialVersionUID = 1L;
	
	
	@Override
	public String getTitle() {
		return "Types Chart";
	}

	@Override
	public JFreeChart initChart() {
		
		JFreeChart chart = ChartFactory.createPieChart3D("Type repartition", getDataSet(), false,true, true);
	
		PiePlot plot = (PiePlot) chart.getPlot();
		plot.setSectionPaint("B", Color.BLACK);
		plot.setSectionPaint("W", Color.WHITE);
		plot.setSectionPaint("U", Color.BLUE);
		plot.setSectionPaint("G", Color.GREEN);
		plot.setSectionPaint("R", Color.RED);
		plot.setSectionPaint("multi", Color.YELLOW);
		plot.setSectionPaint("uncolor", Color.GRAY);

		PieSectionLabelGenerator generator = new StandardPieSectionLabelGenerator("{0} = {1}", new DecimalFormat("0"),
				new DecimalFormat("0.00%"));
		plot.setLabelGenerator(generator);
		
		return chart;
	}


	private PieDataset getDataSet() {
		DefaultPieDataset dataset = new DefaultPieDataset();
		for (Entry<String, Integer> entry : manager.analyseTypes(items).entrySet()) {
			dataset.setValue(entry.getKey(), entry.getValue());
		}

		return dataset;
	}

}
