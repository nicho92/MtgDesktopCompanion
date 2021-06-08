package org.magic.gui.components.charts;

import java.util.Map.Entry;

import org.jfree.chart3d.Chart3D;
import org.jfree.chart3d.Chart3DFactory;
import org.jfree.chart3d.data.PieDataset3D;
import org.jfree.chart3d.data.StandardPieDataset3D;
import org.jfree.chart3d.plot.PiePlot3D;
import org.magic.api.beans.MagicCard;
import org.magic.gui.abstracts.MTGUI3DChartComponent;

public class TypeRepartitionPanel extends  MTGUI3DChartComponent<MagicCard> {

	private static final long serialVersionUID = 1L;
	private Chart3D chart;
	
	@Override
	public String getTitle() {
		return "Types";
	}

	@Override
	protected Chart3D createNewChart() {
		chart= Chart3DFactory.createPieChart(
                "Types", 
                "", 
                getDataSet());
		
		var plot = (PiePlot3D) chart.getPlot();
		plot.setSectionLabelGenerator((PieDataset3D dataset, Comparable<?> key)->"");
		
		return chart;
	}
	
	@Override
	public void refresh() {
		PiePlot3D plot = (PiePlot3D) chart.getPlot();
        plot.setDataset(getDataSet());
		
	}
	
	@SuppressWarnings("unchecked")
	public PieDataset3D<String> getDataSet() {
		var dataset = new StandardPieDataset3D<String>();
		for (Entry<String, Integer> entry : manager.analyseTypes(items).entrySet()) {
			dataset.add(entry.getKey(), entry.getValue());
		}
		return dataset;
		
		
	}

}
