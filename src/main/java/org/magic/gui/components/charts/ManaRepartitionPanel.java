package org.magic.gui.components.charts;

import java.util.Map.Entry;

import javax.swing.ImageIcon;

import org.jfree.chart3d.data.PieDataset3D;
import org.jfree.chart3d.data.StandardPieDataset3D;
import org.jfree.chart3d.plot.StandardColorSource;
import org.magic.api.beans.MagicCard;
import org.magic.api.beans.MagicEdition;
import org.magic.api.beans.enums.EnumColors;
import org.magic.gui.abstracts.charts.Abstract3DPieChart;
import org.magic.services.MTGConstants;

public class ManaRepartitionPanel extends Abstract3DPieChart<MagicCard,EnumColors> {

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
		var source = new StandardColorSource<EnumColors>();

		for(EnumColors c : EnumColors.values())
			source.setColor(c,c.toColor());


		plot.setSectionColorSource(source);

	}

	@Override
	public PieDataset3D<EnumColors> getDataSet() {
		var dataset = new StandardPieDataset3D<EnumColors>();
		for (Entry<EnumColors, Integer> data : manager.analyseColors(items).entrySet()) {
			dataset.add(data.getKey(), data.getValue());
		}
		return dataset;
	}

	
	@Override
	public ImageIcon getIcon() {
		return MTGConstants.ICON_TAB_MANA;
	}


}
