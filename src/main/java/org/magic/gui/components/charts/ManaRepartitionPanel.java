package org.magic.gui.components.charts;

import java.util.Map.Entry;

import org.jfree.chart3d.data.PieDataset3D;
import org.jfree.chart3d.data.StandardPieDataset3D;
import org.jfree.chart3d.plot.StandardColorSource;
import org.magic.api.beans.MagicCard;
import org.magic.api.beans.enums.MTGColor;
import org.magic.gui.abstracts.charts.Abstract3DPieChart;

public class ManaRepartitionPanel extends Abstract3DPieChart<MagicCard,MTGColor> {

	public ManaRepartitionPanel(boolean displayPanel) {
		super(displayPanel);
	}

	private static final long serialVersionUID = 1L;

	@Override
	public String getTitle() {
		return "Mana";
	}

	@Override
	protected void initPlot() {
		var source = new StandardColorSource<MTGColor>();

		for(MTGColor c : MTGColor.values())
			source.setColor(c,c.toColor());


		plot.setSectionColorSource(source);

	}

	@Override
	public PieDataset3D<MTGColor> getDataSet() {
		var dataset = new StandardPieDataset3D<MTGColor>();
		for (Entry<MTGColor, Integer> data : manager.analyseColors(items).entrySet()) {
			dataset.add(data.getKey(), data.getValue());
		}
		return dataset;
	}



}
