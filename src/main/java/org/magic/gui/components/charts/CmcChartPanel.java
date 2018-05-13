package org.magic.gui.components.charts;

import java.awt.BorderLayout;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
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
		
		addComponentListener(new ComponentAdapter() {
			@Override
			public void componentShown(ComponentEvent componentEvent) {
				init(cards);
			}

		});
	}

	public void init(MagicDeck deck) {
		init(deck.getAsList());
	}

	ChartPanel pane;

	private void refresh() {
		this.removeAll();


		if(cards==null)
			return;
		
		JFreeChart chart = ChartFactory.createBarChart("Mana Curve", "cost", "number", getManaCurveDataSet(),
				PlotOrientation.VERTICAL, true, true, false);

		pane = new ChartPanel(chart);

		this.add(pane, BorderLayout.CENTER);
		chart.fireChartChanged();
		pane.revalidate();
	}

	public void init(List<MagicCard> cards) {
		this.cards = cards;
		if(isVisible() && cards!=null)
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
