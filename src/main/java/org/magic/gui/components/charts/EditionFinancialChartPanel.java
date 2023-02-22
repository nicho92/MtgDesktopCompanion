package org.magic.gui.components.charts;

import static org.magic.services.tools.MTG.getEnabledPlugin;

import java.awt.BorderLayout;
import java.util.List;

import javax.swing.JComboBox;

import org.jfree.data.category.CategoryDataset;
import org.jfree.data.category.DefaultCategoryDataset;
import org.magic.api.beans.CardShake;
import org.magic.api.beans.MagicEdition;
import org.magic.api.beans.OrderEntry;
import org.magic.api.beans.enums.TransactionDirection;
import org.magic.api.interfaces.MTGDao;
import org.magic.api.interfaces.MTGDashBoard;
import org.magic.gui.abstracts.charts.Abstract2DBarChart;
import org.magic.services.tools.UITools;

public class EditionFinancialChartPanel extends Abstract2DBarChart<OrderEntry> {

	private static final long serialVersionUID = 1L;
	private JComboBox<MagicEdition> cboEditions;

	public EditionFinancialChartPanel() {
		cboEditions = UITools.createComboboxEditions();
		add(cboEditions, BorderLayout.NORTH);

		cboEditions.addItemListener(il->refresh());
	}

	public void init(MagicEdition ed) {
		cboEditions.setSelectedItem(ed);
		if(isVisible())
			refresh();
	}


	@Override
	public String getTitle() {
		return "Orders by Editions";
	}

	@Override
	public CategoryDataset getDataSet() {

		var dataset = new DefaultCategoryDataset();
		try {
			var ed = (MagicEdition)cboEditions.getSelectedItem();
			items = getEnabledPlugin(MTGDao.class).listOrders().stream().filter(oe->ed.getId().equals(oe.getEdition().getId())).toList();
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


	private double getTotal(List<OrderEntry> order)
	{
		return order.stream().filter(o->o.getTypeTransaction()==TransactionDirection.BUY).mapToDouble(OrderEntry::getItemPrice).sum()-order.stream().filter(o->o.getTypeTransaction()==TransactionDirection.SELL).mapToDouble(OrderEntry::getItemPrice).sum();
	}


}
