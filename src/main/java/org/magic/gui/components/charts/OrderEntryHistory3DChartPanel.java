package org.magic.gui.components.charts;

import java.util.Date;

import org.apache.commons.lang3.time.DateUtils;
import org.jfree.data.time.Day;
import org.jfree.data.time.TimeSeries;
import org.jfree.data.time.TimeSeriesCollection;
import org.magic.api.beans.OrderEntry;
import org.magic.gui.abstracts.charts.Abstract2DHistoChart;

public class OrderEntryHistory3DChartPanel extends Abstract2DHistoChart<OrderEntry> {

	private static final long serialVersionUID = 1L;


	@Override
	public TimeSeriesCollection  getDataSet() {
		var dataset = new TimeSeriesCollection();
		var data = new TimeSeries("Orders");


		for(Date d : items.stream().map(OrderEntry::getTransactionDate).sorted().distinct().toList())
			data.add(new Day(d), items.stream().filter(oe->DateUtils.isSameDay(d,oe.getTransactionDate())).count());

		dataset.addSeries(data);

		return dataset;
	}

	@Override
	public String getTitle() {
		return "Orders history";
	}

}