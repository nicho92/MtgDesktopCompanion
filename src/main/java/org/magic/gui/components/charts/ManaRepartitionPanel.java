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
import org.magic.api.beans.enums.MTGColor;
import org.magic.gui.abstracts.MTGUIChartComponent;

public class ManaRepartitionPanel extends MTGUIChartComponent<MagicCard> {

	private static final long serialVersionUID = 1L;
	
	
	@Override
	public String getTitle() {
		return "Mana Chart";
	}

	@Override
	public JFreeChart initChart() {
		
		JFreeChart chart = ChartFactory.createPieChart3D("Color repartition", 
				getDataSet(), 
				false, 
				true, true);
		
		var plot = (PiePlot) chart.getPlot();
		
		for(MTGColor c : MTGColor.values())
		{
			plot.setSectionPaint(c,c.toColor());
			
		}
		plot.setSectionPaint("Multi", Color.YELLOW);
		plot.setSectionPaint("multi", Color.YELLOW);
		
		plot.setSimpleLabels(true);

		PieSectionLabelGenerator generator = new StandardPieSectionLabelGenerator("{1}", new DecimalFormat("0"),
				new DecimalFormat("0.00%"));
		plot.setLabelGenerator(generator);

		return chart;
	}

	private PieDataset<MTGColor> getDataSet() {
		DefaultPieDataset<MTGColor> dataset = new DefaultPieDataset<>();
		for (Entry<MTGColor, Integer> data : manager.analyseColors(items).entrySet()) {
			dataset.setValue(data.getKey(), data.getValue());

		}

		return dataset;
	}

}
