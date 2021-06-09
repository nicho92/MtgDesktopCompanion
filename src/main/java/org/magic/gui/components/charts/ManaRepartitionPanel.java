package org.magic.gui.components.charts;

import java.util.Map.Entry;

import org.jfree.chart3d.Chart3D;
import org.jfree.chart3d.Chart3DFactory;
import org.jfree.chart3d.data.PieDataset3D;
import org.jfree.chart3d.data.StandardPieDataset3D;
import org.jfree.chart3d.plot.PiePlot3D;
import org.jfree.chart3d.plot.StandardColorSource;
import org.magic.api.beans.MagicCard;
import org.magic.api.beans.enums.MTGColor;
import org.magic.gui.abstracts.MTGUI3DChartComponent;

public class ManaRepartitionPanel extends MTGUI3DChartComponent<MagicCard> {

	private static final long serialVersionUID = 1L;
	private Chart3D chart;
	
	
	@Override
	public String getTitle() {
		return "Mana";
	}

	
	@Override
	protected Chart3D createNewChart() {
		 chart= Chart3DFactory.createPieChart(
                getTitle(), 
                "", 
                getDataSet());
		
		var plot = (PiePlot3D) chart.getPlot();
		var source = new StandardColorSource<MTGColor>();
		
		for(MTGColor c : MTGColor.values())
			source.setColor(c,c.toColor());

	
		plot.setSectionColorSource(source);
		plot.setSectionLabelGenerator((PieDataset3D dataset, Comparable<?> key)->"");
			
		return chart;
	}
	
	@Override
	public void refresh() {
		PiePlot3D plot = (PiePlot3D) chart.getPlot();
        plot.setDataset(getDataSet());
    	plot.setToolTipGenerator((PieDataset3D dataset, Comparable<?> key)->String.valueOf(key + ":" + dataset.getValue(key)));
    	chartPanel.zoomToFit();
    }
	
	
	public PieDataset3D<MTGColor> getDataSet() {
		var dataset = new StandardPieDataset3D<MTGColor>();
		for (Entry<MTGColor, Integer> data : manager.analyseColors(items).entrySet()) {
			dataset.add(data.getKey(), data.getValue());
		}
		return dataset;
	}

}
