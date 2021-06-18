package org.magic.gui.abstracts.charts;

import org.jfree.chart3d.Chart3DFactory;
import org.jfree.chart3d.Colors;
import org.jfree.chart3d.plot.PiePlot3D;

public abstract class Abstract3DBarChart<B> extends MTGUI3DChartComponent<B> {

	private static final long serialVersionUID = 1L;
	protected PiePlot3D plot;
	
	@Override
	public void refresh() {
		initPlot();
        plot.setDataset(getDataSet());
    	chartPanel.zoomToFit();
	}

	protected void initPlot()
	{
		plot.setSectionColors(Colors.createPastelColors());
	}

	@Override
	@SuppressWarnings({ "rawtypes"})
	protected void createNewChart() {
		chart = Chart3DFactory.createBarChart(
                getTitle(), 
                "", 
                getDataSet(), 
                "", "", "");
	
		
	}
	

	

}