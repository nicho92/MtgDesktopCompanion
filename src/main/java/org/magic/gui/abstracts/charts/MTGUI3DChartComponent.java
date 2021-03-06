package org.magic.gui.abstracts.charts;

import java.awt.BorderLayout;
import java.awt.Color;

import org.jfree.chart3d.Chart3D;
import org.jfree.chart3d.Chart3DPanel;
import org.jfree.chart3d.Orientation;
import org.jfree.chart3d.data.Dataset3D;
import org.jfree.chart3d.graphics2d.Anchor2D;
import org.jfree.chart3d.graphics3d.swing.DisplayPanel3D;
import org.jfree.chart3d.legend.LegendAnchor;
import org.magic.services.MTGControler;

public abstract class MTGUI3DChartComponent<T, U extends Dataset3D> extends AbstractChartComponent<T> {


	private static final long serialVersionUID = 1L;
	protected Chart3DPanel chartPanel;
	protected Chart3D chart;

	
	public abstract U getDataSet() ;

	
	protected MTGUI3DChartComponent() {
		super();
		onlyOneRefresh=false;
		init();
	}
	
	

	
	public boolean showDisplayPanel()
	{
		return true;
	}
	
	
	
	private void init() {
		
		createNewChart();
 		
		chart.setTitleAnchor(Anchor2D.TOP_CENTER);
		chart.setLegendPosition(LegendAnchor.BOTTOM_CENTER,Orientation.HORIZONTAL);
		chart.setTitle(getTitle(),MTGControler.getInstance().getFont().deriveFont(20f),Color.BLACK);
		
		chartPanel = new Chart3DPanel(chart);
		chartPanel.setMargin(0.05);	
		
		if(showDisplayPanel())
			add( new DisplayPanel3D(chartPanel), BorderLayout.CENTER);
		else
			add( chartPanel, BorderLayout.CENTER);
		
		
		
		
	}
	
	
}
