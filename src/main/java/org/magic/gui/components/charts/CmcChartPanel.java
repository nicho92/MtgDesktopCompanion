package org.magic.gui.components.charts;

import java.awt.BorderLayout;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.swing.JPanel;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.category.CategoryDataset;
import org.jfree.data.category.DefaultCategoryDataset;
import org.magic.api.beans.MagicCard;
import org.magic.api.beans.MagicDeck;
import org.magic.services.MTGDeckManager;

public class CmcChartPanel extends JPanel {

	private List<MagicCard> cards;
	private transient MTGDeckManager manager;

	public CmcChartPanel() {
		setLayout(new BorderLayout(0, 0));
		manager = new MTGDeckManager();
	}

	public void init(MagicDeck deck) {
		cards = new ArrayList<>();
		if (deck != null && deck.getMap() != null)
			cards = deck.getAsList();
		refresh();
	}

	ChartPanel pane;

	private void refresh() {
		this.removeAll();

		JFreeChart chart = ChartFactory.createBarChart("Mana Curve", "cost", "number", getManaCurveDataSet(),
				PlotOrientation.VERTICAL, true, true, false);

		pane = new ChartPanel(chart);

		this.add(pane, BorderLayout.CENTER);
		chart.fireChartChanged();
	}

	public void init(List<MagicCard> cards) {
		this.cards = cards;
		refresh();
	}

	private CategoryDataset getManaCurveDataSet() {
		DefaultCategoryDataset dataset = new DefaultCategoryDataset();
		Map<Integer, Integer> temp = manager.analyseCMC(cards);

		for (Entry<Integer, Integer> k : temp.entrySet())
			dataset.addValue(k.getValue(), "cmc", k.getKey());

		return dataset;
	}

}
