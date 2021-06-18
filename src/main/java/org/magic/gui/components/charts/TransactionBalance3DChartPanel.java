package org.magic.gui.components.charts;

import org.jfree.chart3d.Chart3DFactory;
import org.jfree.chart3d.Colors;
import org.jfree.chart3d.data.DefaultKeyedValues;
import org.jfree.chart3d.data.category.CategoryDataset3D;
import org.jfree.chart3d.data.category.StandardCategoryDataset3D;
import org.jfree.chart3d.plot.CategoryPlot3D;
import org.magic.api.beans.MagicCardStock;
import org.magic.api.beans.Transaction;
import org.magic.gui.abstracts.charts.MTGUI3DChartComponent;

public class TransactionBalance3DChartPanel extends MTGUI3DChartComponent<Transaction> {
	
	private static final String BALANCE = "Balance";
	private static final long serialVersionUID = 1L;

	public CategoryDataset3D<String,Double, String>  getDataSet() {
		var dataset = new StandardCategoryDataset3D<String,Double, String>();
		var serieB = new DefaultKeyedValues<String, Double>();
		var serieS = new DefaultKeyedValues<String, Double>();
		
		serieB.put(BALANCE,0.0);
		serieS.put(BALANCE,0.0);
		
		
		for (Transaction t : items)
			for(MagicCardStock mcs : t.getItems())
				{
					if(mcs.getPrice()>0)
						serieB.put(BALANCE,serieB.getValue(BALANCE)+(mcs.getPrice()*mcs.getQte()));
					else
						serieS.put(BALANCE,serieS.getValue(BALANCE)+(mcs.getPrice()*mcs.getQte()));
				}
		
		dataset.addSeriesAsRow("Sell",serieS);
		dataset.addSeriesAsRow("Buy",serieB);
		
		return dataset;
	}

	@Override
	public String getTitle() {
		return BALANCE;
	}


	@Override
	protected void createNewChart() {
		chart = Chart3DFactory.createBarChart(
                getTitle(), 
                "", 
                getDataSet(), 
                "", "", "");
	}


	@Override
	public void refresh() {
		CategoryPlot3D  plot = (CategoryPlot3D ) chart.getPlot();
        plot.setDataset(getDataSet());
        plot.getRenderer().setColors(Colors.createPimpColors());
		chartPanel.zoomToFit();
	}

}