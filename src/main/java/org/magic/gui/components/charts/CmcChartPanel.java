package org.magic.gui.components.charts;

import java.util.Map.Entry;

import org.jfree.chart3d.Chart3D;
import org.jfree.chart3d.Chart3DFactory;
import org.jfree.chart3d.Colors;
import org.jfree.chart3d.data.DefaultKeyedValues;
import org.jfree.chart3d.data.category.CategoryDataset3D;
import org.jfree.chart3d.data.category.StandardCategoryDataset3D;
import org.jfree.chart3d.plot.CategoryPlot3D;
import org.magic.api.beans.MagicCard;
import org.magic.gui.abstracts.MTGUI3DChartComponent;

public class CmcChartPanel extends MTGUI3DChartComponent<MagicCard> {
	
	private static final long serialVersionUID = 1L;
	private Chart3D chart;

	private CategoryDataset3D<String,Integer,Integer>  getDataSet() {
		var dataset = new StandardCategoryDataset3D<String, Integer,Integer>();
		var serie = new DefaultKeyedValues<Integer, Integer>();
		
		for (Entry<Integer, Integer> k : manager.analyseCMC(items).entrySet())
			serie.put(k.getKey(),k.getValue());
		
		dataset.addSeriesAsRow("cmc", serie);
		
		
		return dataset;
	}

	@Override
	public String getTitle() {
		return "CMC";
	}


	@Override
	protected Chart3D createNewChart() {
		chart = Chart3DFactory.createBarChart(
                getTitle(), 
                "", 
                getDataSet(), 
                "", "cost", "");
		
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
