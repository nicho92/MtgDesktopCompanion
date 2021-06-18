package org.magic.gui.abstracts;

import org.jfree.chart3d.Chart3D;
import org.jfree.chart3d.Chart3DFactory;
import org.jfree.chart3d.Colors;
import org.jfree.chart3d.Orientation;
import org.jfree.chart3d.data.PieDataset3D;
import org.jfree.chart3d.graphics2d.Anchor2D;
import org.jfree.chart3d.plot.PiePlot3D;
import org.jfree.chart3d.plot.Plot3D;
import org.jfree.chart3d.style.ChartStyle;
import org.jfree.chart3d.table.TextElement;

public abstract class Abstract3DPieChart<B> extends MTGUI3DChartComponent<B> {

	private static final long serialVersionUID = 1L;
	protected Chart3D chart;
	protected PiePlot3D plot;
	
	@Override
	public void refresh() {
		initPlot();
        plot.setDataset(getDataSet());
    	chartPanel.zoomToFit();
	}
	
	public boolean viewLabel()
	{
		return true;
	}
	
	public boolean viewLegend()
	{
		return true;
	}
	
	protected void initPlot()
	{
		plot.setSectionColors(Colors.createPastelColors());
	}

	@Override
	@SuppressWarnings({ "rawtypes", "unchecked" })
	protected Chart3D createNewChart() {
		chart= Chart3DFactory.createPieChart(
                getTitle(), 
                "", 
                getDataSet());
		
		
		
		plot = (PiePlot3D) chart.getPlot();
		
		
		if(!viewLegend())
			chart.setLegendBuilder((Plot3D plot, Anchor2D anchor, Orientation orientation, ChartStyle style)->new TextElement(""));
			
			
		
		
		if(viewLabel())
			plot.setSectionLabelGenerator(( PieDataset3D dataset, Comparable<?> key)->key.toString());
		else
			plot.setSectionLabelGenerator(( PieDataset3D dataset, Comparable<?> key)->"");
		
		
		plot.setToolTipGenerator((PieDataset3D dataset, Comparable<?> key)->String.valueOf(key + ":" + dataset.getValue(key)));
		
		
		return chart;
	}
	

	public abstract PieDataset3D<?> getDataSet() ;

	

}