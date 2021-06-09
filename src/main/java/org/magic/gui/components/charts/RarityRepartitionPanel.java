package org.magic.gui.components.charts;

import java.util.Map.Entry;

import org.jfree.chart3d.Chart3D;
import org.jfree.chart3d.Chart3DFactory;
import org.jfree.chart3d.data.PieDataset3D;
import org.jfree.chart3d.data.StandardPieDataset3D;
import org.jfree.chart3d.plot.PiePlot3D;
import org.jfree.chart3d.plot.StandardColorSource;
import org.magic.api.beans.MagicCard;
import org.magic.api.beans.enums.MTGRarity;
import org.magic.gui.abstracts.MTGUI3DChartComponent;

public class RarityRepartitionPanel extends MTGUI3DChartComponent<MagicCard> {

	private static final long serialVersionUID = 1L;
	private Chart3D chart;


	@Override
	public String getTitle() {
		return "Rarity";
	}
	
	@Override
	protected Chart3D createNewChart() {
		chart= Chart3DFactory.createPieChart(
                getTitle(), 
                "", 
                getDataSet());
		
		var plot = (PiePlot3D) chart.getPlot();
		var source = new StandardColorSource<String>();
		
		for(MTGRarity r : MTGRarity.values())
			source.setColor(r.toPrettyString(),r.toColor());
		
		plot.setSectionColorSource(source);
		plot.setSectionLabelGenerator((PieDataset3D dataset, Comparable<?> key)->"");
	  	plot.setToolTipGenerator((PieDataset3D dataset, Comparable<?> key)->String.valueOf(key + ":" + dataset.getValue(key)));
	  	 
		return chart;
	}
	

	public PieDataset3D<String> getDataSet() {
		
		var dataset = new StandardPieDataset3D<String>();
		for (Entry<MTGRarity, Integer> data : manager.analyseRarities(items).entrySet()) {
			dataset.add(data.getKey().toPrettyString(), data.getValue());
		}
		return dataset;
	}

	@Override
	public void refresh() {
		PiePlot3D plot = (PiePlot3D) chart.getPlot();
        plot.setDataset(getDataSet());
    	chartPanel.zoomToFit();
	}
	


}
