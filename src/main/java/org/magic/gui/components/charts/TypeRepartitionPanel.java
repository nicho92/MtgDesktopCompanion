package org.magic.gui.components.charts;

import java.util.Map.Entry;

import org.jfree.chart3d.Chart3D;
import org.jfree.chart3d.Chart3DFactory;
import org.jfree.chart3d.Chart3DPanel;
import org.jfree.chart3d.Orientation;
import org.jfree.chart3d.data.PieDataset3D;
import org.jfree.chart3d.data.StandardPieDataset3D;
import org.jfree.chart3d.graphics2d.Anchor2D;
import org.jfree.chart3d.legend.LegendAnchor;
import org.magic.api.beans.MagicCard;
import org.magic.gui.abstracts.MTGUI3DChartComponent;

public class TypeRepartitionPanel extends  MTGUI3DChartComponent<MagicCard> {

	private static final long serialVersionUID = 1L;
	
	
	@Override
	public String getTitle() {
		return "Types Chart";
	}

	@Override
	public Chart3D initChart() {
		var chart = Chart3DFactory.createPieChart(
		                "Types", 
		                "", 
		                getDataSet());
		 
		chart.setTitleAnchor(Anchor2D.TOP_CENTER);
		chart.setLegendPosition(LegendAnchor.BOTTOM_CENTER,Orientation.HORIZONTAL);
		chartPanel = new Chart3DPanel(chart);
		chartPanel.setMargin(0.05);
		
		
		return chart;
	}


	private PieDataset3D<String> getDataSet() {
		var dataset = new StandardPieDataset3D<String>();
		for (Entry<String, Integer> entry : manager.analyseTypes(items).entrySet()) {
			dataset.add(entry.getKey(), entry.getValue());
		}

		
		return dataset;
	}

}
