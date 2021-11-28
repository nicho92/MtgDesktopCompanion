package org.magic.gui.components.charts;

import java.util.Map.Entry;

import org.jfree.chart3d.data.PieDataset3D;
import org.jfree.chart3d.data.StandardPieDataset3D;
import org.jfree.chart3d.plot.StandardColorSource;
import org.magic.api.beans.MagicCard;
import org.magic.api.beans.enums.MTGRarity;
import org.magic.gui.abstracts.charts.Abstract3DPieChart;

public class RarityRepartitionPanel extends Abstract3DPieChart<MagicCard,String> {

	public RarityRepartitionPanel(boolean displayPanel) {
		super(displayPanel);
	}

	private static final long serialVersionUID = 1L;
	
	@Override
	public String getTitle() {
		return "Rarity";
	}
	
	
	@Override
	protected void initPlot() {
		var source = new StandardColorSource<String>();
		
		for(MTGRarity r : MTGRarity.values())
			source.setColor(r.toPrettyString(),r.toColor());
		
		plot.setSectionColorSource(source);
		
	}
	
	public PieDataset3D<String> getDataSet() {
		
		var dataset = new StandardPieDataset3D<String>();
		for (Entry<MTGRarity, Integer> data : manager.analyseRarities(items).entrySet()) {
			dataset.add(data.getKey().toPrettyString(), data.getValue());
		}
		return dataset;
	}

}
