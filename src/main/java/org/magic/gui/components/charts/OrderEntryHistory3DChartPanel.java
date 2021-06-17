package org.magic.gui.components.charts;

import java.util.Date;
import java.util.stream.Collectors;

import org.apache.commons.lang3.time.DateUtils;
import org.jfree.chart3d.Chart3D;
import org.jfree.chart3d.Chart3DFactory;
import org.jfree.chart3d.Colors;
import org.jfree.chart3d.data.DefaultKeyedValues;
import org.jfree.chart3d.data.category.CategoryDataset3D;
import org.jfree.chart3d.data.category.StandardCategoryDataset3D;

import org.jfree.chart3d.plot.CategoryPlot3D;
import org.magic.api.beans.OrderEntry;
import org.magic.gui.abstracts.MTGUI3DChartComponent;

public class OrderEntryHistory3DChartPanel extends MTGUI3DChartComponent<OrderEntry> {
	
	private static final long serialVersionUID = 1L;
	private Chart3D chart;
	
	private CategoryDataset3D<String,Long, Date>  getDataSet() {
		var dataset = new StandardCategoryDataset3D<String,Long,Date>();
		
	
		DefaultKeyedValues<Date, Long> data = new DefaultKeyedValues<>();
		
		
		for(Date d : items.stream().map(OrderEntry::getTransactionDate).sorted().distinct().collect(Collectors.toList()))
			data.put(d, items.stream().filter(oe->DateUtils.isSameDay(d,oe.getTransactionDate())).count());
		
		dataset.addSeriesAsRow("Orders",data);
		
		return dataset;
	}
	
	@Override
	public String getTitle() {
		return "Orders history";
	}


	@Override
	protected Chart3D createNewChart() {
		chart = Chart3DFactory.createLineChart(
                getTitle(), 
                "", 
                getDataSet(), 
                "", "", "");
		
		return chart;
	}


	@Override
	public void refresh() {
		CategoryPlot3D  plot = (CategoryPlot3D ) chart.getPlot();
        plot.setDataset(getDataSet());
        plot.getRenderer().setColors(Colors.createPimpColors());
		chartPanel.zoomToFit();
	}

}