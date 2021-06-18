package org.magic.gui.components.charts;

import static org.magic.tools.MTG.getEnabledPlugin;

import java.awt.BorderLayout;
import java.util.List;

import javax.swing.JComboBox;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.category.CategoryDataset;
import org.jfree.data.category.DefaultCategoryDataset;
import org.magic.api.beans.CardShake;
import org.magic.api.beans.MagicEdition;
import org.magic.api.beans.OrderEntry;
import org.magic.api.beans.OrderEntry.TYPE_TRANSACTION;
import org.magic.api.interfaces.MTGDao;
import org.magic.api.interfaces.MTGDashBoard;
import org.magic.gui.abstracts.charts.MTGUI2DChartComponent;
import org.magic.tools.UITools;

public class EditionFinancialChartPanel extends MTGUI2DChartComponent<OrderEntry> {

	private static final long serialVersionUID = 1L;
	private JComboBox<MagicEdition> cboEditions;

	public EditionFinancialChartPanel() {
		cboEditions = UITools.createComboboxEditions();
		add(cboEditions, BorderLayout.NORTH);
		
		cboEditions.addItemListener(il->refresh());
	}

	public void createNewChart() {

		if(cboEditions.getSelectedItem()==null)
			return;
		
		chart = ChartFactory.createBarChart("BALANCE", "MOUV", "VALUE", getDataSet(),PlotOrientation.VERTICAL, true, true, false);
	}

	public void init(MagicEdition ed) {
		cboEditions.setSelectedItem(ed);
		if(isVisible())
			refresh();
	}

	private CategoryDataset getDataSet() {
		
		var dataset = new DefaultCategoryDataset();
		try {
			var ed = (MagicEdition)cboEditions.getSelectedItem();
			items = getEnabledPlugin(MTGDao.class).listOrderForEdition(ed);
			var price = getEnabledPlugin(MTGDashBoard.class).getShakesForEdition(ed);
			double totalEd = price.getShakes().stream().mapToDouble(CardShake::getPrice).sum();
			
			if(!items.isEmpty()) {
				dataset.addValue(totalEd, "Actual Value", ed.getSet() );
				dataset.addValue(getTotal(items), "Paid", ed.getSet() );
			}
		}
		catch(Exception e)
		{
			logger.error("erreur calculate dataset",e);
		}
		return dataset;
	}

	
	public double getTotal(List<OrderEntry> order)
	{
		return order.stream().filter(o->o.getTypeTransaction()==TYPE_TRANSACTION.BUY).mapToDouble(OrderEntry::getItemPrice).sum()-order.stream().filter(o->o.getTypeTransaction()==TYPE_TRANSACTION.SELL).mapToDouble(OrderEntry::getItemPrice).sum();
	}

	@Override
	public String getTitle() {
		return "Orders by Editions";
	}
	
	
}
