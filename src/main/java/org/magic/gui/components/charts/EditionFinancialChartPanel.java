package org.magic.gui.components.charts;

import java.awt.BorderLayout;
import java.util.List;

import javax.swing.JComboBox;
import javax.swing.JPanel;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.category.CategoryDataset;
import org.jfree.data.category.DefaultCategoryDataset;
import org.magic.api.beans.CardShake;
import org.magic.api.beans.MagicEdition;
import org.magic.api.beans.OrderEntry;
import org.magic.api.interfaces.MTGDashBoard;
import org.magic.services.MTGControler;
import org.magic.tools.UITools;

public class EditionFinancialChartPanel extends JPanel {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private JComboBox<MagicEdition> cboEditions;
	
	public EditionFinancialChartPanel() {
		setLayout(new BorderLayout(0, 0));
		JPanel panelEdition = new JPanel();
		add(panelEdition, BorderLayout.NORTH);
		cboEditions = UITools.createComboboxEditions();
		panelEdition.add(cboEditions);
		
		
		cboEditions.addItemListener(il->refresh());
	}

	ChartPanel pane;

	private void refresh() {
		this.removeAll();

		if(cboEditions.getSelectedItem()==null)
			return;
		
		JFreeChart chart = ChartFactory.createBarChart("BALANCE", "MOUV", "VALUE", getDataSet(),PlotOrientation.VERTICAL, true, true, false);
		pane = new ChartPanel(chart);
		this.add(pane, BorderLayout.CENTER);
		chart.fireChartChanged();
		pane.revalidate();
	}

	public void init(MagicEdition ed) {
		cboEditions.setSelectedItem(ed);
		if(isVisible())
			refresh();
	}

	private CategoryDataset getDataSet() {
		
		DefaultCategoryDataset dataset = new DefaultCategoryDataset();
		try {
			MagicEdition ed = (MagicEdition)cboEditions.getSelectedItem();
			List<OrderEntry> temp = MTGControler.getInstance().getFinancialService().getOrderFor(ed);
			List<CardShake> price = MTGControler.getInstance().getEnabled(MTGDashBoard.class).getShakesForEdition(ed);
			double totalEd = price.stream().mapToDouble(CardShake::getPrice).sum();
			
			totalEd = MTGControler.getInstance().getCurrencyService().convert(MTGControler.getInstance().getEnabled(MTGDashBoard.class).getCurrency(),temp.get(0).getCurrency(), totalEd);
			dataset.addValue(totalEd, "Actual Value", ed.getSet() );
			dataset.addValue(MTGControler.getInstance().getFinancialService().getTotal(temp), "Paid", ed.getSet() );
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return dataset;
	}

}
