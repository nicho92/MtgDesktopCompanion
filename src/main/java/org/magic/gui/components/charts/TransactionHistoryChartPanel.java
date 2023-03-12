package org.magic.gui.components.charts;

import java.util.Date;

import org.apache.commons.lang3.time.DateUtils;
import org.jfree.data.time.Day;
import org.jfree.data.time.TimeSeries;
import org.jfree.data.time.TimeSeriesCollection;
import org.magic.api.beans.enums.TransactionDirection;
import org.magic.api.beans.shop.Transaction;
import org.magic.gui.abstracts.charts.Abstract2DHistoChart;

public class TransactionHistoryChartPanel extends Abstract2DHistoChart<Transaction> {

	private static final long serialVersionUID = 1L;


	@Override
	public TimeSeriesCollection  getDataSet() {
		var dataset = new TimeSeriesCollection();
		var dataSell = new TimeSeries("Sell");
		var dataBuy = new TimeSeries("Buy");
		
		for(Date d : items.stream().map(Transaction::getDateCreation).distinct().sorted().toList())
		{
			dataBuy.add(new Day(d), items.stream().filter(oe->oe.getTypeTransaction()==TransactionDirection.BUY).filter(oe->DateUtils.isSameDay(d,oe.getDateCreation())).mapToDouble(Transaction::total).sum());
			dataSell.add(new Day(d), items.stream().filter(oe->oe.getTypeTransaction()==TransactionDirection.SELL).filter(oe->DateUtils.isSameDay(d,oe.getDateCreation())).mapToDouble(Transaction::total).sum());
		}

		dataset.addSeries(dataSell);
		dataset.addSeries(dataBuy);

		return dataset;
	}

	@Override
	public String getTitle() {
		return "Transaction history";
	}

}