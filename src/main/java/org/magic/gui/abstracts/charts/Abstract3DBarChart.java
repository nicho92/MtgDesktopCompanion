package org.magic.gui.abstracts.charts;

import org.jfree.chart3d.Chart3DFactory;
import org.jfree.chart3d.data.category.CategoryDataset3D;
import org.jfree.chart3d.plot.CategoryPlot3D;

public abstract class Abstract3DBarChart<B,C extends Comparable<C>,D extends Comparable<D>,E extends Comparable<E>> extends MTGUI3DChartComponent<B,CategoryDataset3D<C,D,E>> {

	protected Abstract3DBarChart(boolean displayPanel) {
		super(displayPanel);
		}

	private static final long serialVersionUID = 1L;
	protected CategoryPlot3D plot;
	
	@Override
	public void refresh() {
		initPlot();
        plot.setDataset(getDataSet());
    	chartPanel.zoomToFit();
	}

	@Override
	protected void createNewChart() {
		chart = Chart3DFactory.createBarChart(
                getTitle(), 
                "", 
                getDataSet(), 
                "", "", "");
	
		
		plot = (CategoryPlot3D) chart.getPlot();
	}
	

	

}