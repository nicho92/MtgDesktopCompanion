package org.magic.gui.abstracts.charts;

import java.awt.BorderLayout;

import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.data.general.Dataset;

public abstract class MTGUI2DChartComponent<T> extends AbstractChartComponent<T> {


	private static final long serialVersionUID = 1L;
	protected ChartPanel chartPanel;
	protected JFreeChart chart;

	protected MTGUI2DChartComponent() {
		super();
		onlyOneRefresh=false;
		init();
	}
	

	public void refresh()
	{

		if(items==null)
			return;
		
		createNewChart();
		
		
		if(!showLegend())
			chart.removeLegend();
		
		chartPanel.setChart(chart);
		
		if(chart!=null)
			chart.fireChartChanged();

	}
	
	
	public abstract <U extends Dataset> U getDataSet() ;

	
	private void init() {
		chartPanel = new ChartPanel(null,true);
		add(chartPanel, BorderLayout.CENTER);
		
		chartPanel.addMouseWheelListener(mwe -> {
			if (mwe.getWheelRotation() > 0) {
				chartPanel.zoomOutDomain(0.5, 0.5);

			} else if (mwe.getWheelRotation() < 0) {
				chartPanel.zoomInDomain(1.5, 1.5);
			}
		});
		

	}
	
	

}
