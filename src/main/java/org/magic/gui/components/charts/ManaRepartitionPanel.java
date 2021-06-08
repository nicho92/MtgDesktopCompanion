package org.magic.gui.components.charts;

import java.util.Map.Entry;

import org.jfree.chart3d.Chart3D;
import org.jfree.chart3d.Chart3DFactory;
import org.jfree.chart3d.Orientation;
import org.jfree.chart3d.data.PieDataset3D;
import org.jfree.chart3d.data.StandardPieDataset3D;
import org.jfree.chart3d.graphics2d.Anchor2D;
import org.jfree.chart3d.legend.LegendAnchor;
import org.jfree.chart3d.plot.PiePlot3D;
import org.jfree.chart3d.plot.StandardColorSource;
import org.magic.api.beans.MagicCard;
import org.magic.api.beans.enums.MTGColor;
import org.magic.gui.abstracts.MTGUI3DChartComponent;

public class ManaRepartitionPanel extends MTGUI3DChartComponent<MagicCard> {

	private static final long serialVersionUID = 1L;
	
	
	@Override
	public String getTitle() {
		return "Mana Chart";
	}

	@Override
	public Chart3D initChart() {
		
		var chart = Chart3DFactory.createPieChart(
                "Mana", 
                "", 
                getDataSet());
		
		chart.setTitleAnchor(Anchor2D.TOP_CENTER);
		chart.setLegendPosition(LegendAnchor.BOTTOM_CENTER,Orientation.HORIZONTAL);
		
		var plot = (PiePlot3D) chart.getPlot();
		var source = new StandardColorSource<MTGColor>();
		
		for(MTGColor c : MTGColor.values())
			source.setColor(c,c.toColor());

	
		plot.setSectionColorSource(source);
		
		return chart;
	}

	private PieDataset3D<MTGColor> getDataSet() {
		var dataset = new StandardPieDataset3D<MTGColor>();
		for (Entry<MTGColor, Integer> data : manager.analyseColors(items).entrySet()) {
			dataset.add(data.getKey(), data.getValue());
		}
		return dataset;
	}

}
