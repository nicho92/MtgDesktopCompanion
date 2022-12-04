package org.magic.gui.components.charts;

import java.util.Date;

import org.apache.commons.lang3.time.DateUtils;
import org.jfree.data.time.Day;
import org.jfree.data.time.TimeSeries;
import org.jfree.data.time.TimeSeriesCollection;
import org.magic.api.beans.OrderEntry;
import org.magic.api.beans.enums.TransactionDirection;
import org.magic.gui.abstracts.charts.Abstract2DHistoChart;

public class OrderEntryHistoryChartPanel extends Abstract2DHistoChart<OrderEntry> {

	private static final long serialVersionUID = 1L;


	@Override
	public TimeSeriesCollection  getDataSet() {
		var dataset = new TimeSeriesCollection();
		var dataSell = new TimeSeries("Sell");
		var dataBuy = new TimeSeries("Buy");

		
		
		
		for(Date d : items.stream().map(OrderEntry::getTransactionDate).distinct().sorted().toList())
		{
			dataBuy.add(new Day(d), items.stream().filter(oe->oe.getTypeTransaction()==TransactionDirection.BUY).filter(oe->DateUtils.isSameDay(d,oe.getTransactionDate())).mapToDouble(OrderEntry::getItemPrice).sum());
			dataSell.add(new Day(d), items.stream().filter(oe->oe.getTypeTransaction()==TransactionDirection.SELL).filter(oe->DateUtils.isSameDay(d,oe.getTransactionDate())).mapToDouble(OrderEntry::getItemPrice).sum());
		}

		dataset.addSeries(dataSell);
		dataset.addSeries(dataBuy);

		return dataset;
	}

	@Override
	public String getTitle() {
		return "Orders history";
	}

}