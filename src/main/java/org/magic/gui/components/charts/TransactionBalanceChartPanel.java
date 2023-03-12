package org.magic.gui.components.charts;

import org.jfree.data.category.CategoryDataset;
import org.jfree.data.category.DefaultCategoryDataset;
import org.magic.api.beans.enums.TransactionDirection;
import org.magic.api.beans.shop.Transaction;
import org.magic.gui.abstracts.charts.Abstract2DBarChart;

public class TransactionBalanceChartPanel extends Abstract2DBarChart<Transaction> {

	private static final String BALANCE = "Balance";
	private static final long serialVersionUID = 1L;

	@Override
	public CategoryDataset  getDataSet() {
		var dataset = new DefaultCategoryDataset();
					for(var type : TransactionDirection.values())
					{
						dataset.addValue(items.stream().filter(t->t.getTypeTransaction()==type).mapToDouble(Transaction::total).sum(), BALANCE, type.name());
					}
					

		return dataset;
	}

	@Override
	public String getTitle() {
		return BALANCE;
	}

}