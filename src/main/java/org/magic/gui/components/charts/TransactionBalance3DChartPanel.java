package org.magic.gui.components.charts;

import org.jfree.chart3d.Colors;
import org.jfree.chart3d.data.DefaultKeyedValues;
import org.jfree.chart3d.data.category.CategoryDataset3D;
import org.jfree.chart3d.data.category.StandardCategoryDataset3D;
import org.jfree.chart3d.plot.CategoryPlot3D;
import org.magic.api.beans.enums.TransactionDirection;
import org.magic.api.beans.shop.Transaction;
import org.magic.api.interfaces.MTGStockItem;
import org.magic.gui.abstracts.charts.Abstract3DBarChart;

public class TransactionBalance3DChartPanel extends Abstract3DBarChart<Transaction,String,Double, String> {

	public TransactionBalance3DChartPanel(boolean displayPanel) {
		super(displayPanel);
		}

	private static final String BALANCE = "Balance";
	private static final long serialVersionUID = 1L;

	@Override
	public CategoryDataset3D<String,Double, String>  getDataSet() {
		var dataset = new StandardCategoryDataset3D<String,Double, String>();
		var serieB = new DefaultKeyedValues<String, Double>();
		var serieS = new DefaultKeyedValues<String, Double>();

		serieB.put(BALANCE,0.0);
		serieS.put(BALANCE,0.0);


		for (Transaction t : items)
				{
					if(t.getTypeTransaction()==TransactionDirection.BUY)
						serieB.put(BALANCE,serieB.getValue(BALANCE)+t.total());
					else
						serieS.put(BALANCE,serieS.getValue(BALANCE)+t.total());
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
	public void refresh() {
		CategoryPlot3D  plot = (CategoryPlot3D ) chart.getPlot();
        plot.setDataset(getDataSet());
        plot.getRenderer().setColors(Colors.createPimpColors());
		chartPanel.zoomToFit();
	}

}