package org.magic.gui.components.charts;

import java.util.Map.Entry;

import javax.swing.ImageIcon;

import org.jfree.chart3d.data.PieDataset3D;
import org.jfree.chart3d.data.StandardPieDataset3D;
import org.jfree.chart3d.plot.StandardColorSource;
import org.magic.api.beans.MTGCard;
import org.magic.api.beans.enums.EnumRarity;
import org.magic.gui.abstracts.charts.Abstract3DPieChart;
import org.magic.services.MTGConstants;

public class RarityRepartitionPanel extends Abstract3DPieChart<MTGCard,String> {

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

		for(EnumRarity r : EnumRarity.values())
			source.setColor(r.toPrettyString(),r.getColor());

		plot.setSectionColorSource(source);

	}

	@Override
	public PieDataset3D<String> getDataSet() {

		var dataset = new StandardPieDataset3D<String>();
		for (Entry<EnumRarity, Integer> data : manager.analyseRarities(items).entrySet()) {
			dataset.add(data.getKey().toPrettyString(), data.getValue());
		}
		return dataset;
	}
	
	@Override
	public ImageIcon getIcon() {
		return MTGConstants.ICON_TAB_RARITY;
	}

}
