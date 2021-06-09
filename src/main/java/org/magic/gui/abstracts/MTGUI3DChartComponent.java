package org.magic.gui.abstracts;

import java.awt.BorderLayout;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.jfree.chart3d.Chart3D;
import org.jfree.chart3d.Chart3DPanel;
import org.jfree.chart3d.Orientation;
import org.jfree.chart3d.graphics2d.Anchor2D;
import org.jfree.chart3d.graphics3d.swing.DisplayPanel3D;
import org.jfree.chart3d.legend.LegendAnchor;
import org.magic.services.MTGDeckManager;

public abstract class MTGUI3DChartComponent<T> extends MTGUIComponent {


	private static final long serialVersionUID = 1L;
	protected transient List<T> items;
	protected transient MTGDeckManager manager;
	protected Chart3DPanel chartPanel;
	
	protected MTGUI3DChartComponent() {
		onlyOneRefresh=false;
		setLayout(new BorderLayout());
		init();
	}
	
	
	
	
	public void init() {
		items = new ArrayList<>();
		manager = new MTGDeckManager();
		
		chartPanel = new Chart3DPanel(initChart());
		add( new DisplayPanel3D(chartPanel), BorderLayout.CENTER);
		chartPanel.setMargin(0.05);
		
	}
	
	

	@Override
	public void onFirstShowing() {
		init(items);
	}
	
	
	
	public void init(Set<T> items)
	{
		this.items = new ArrayList<>(items);
	
		if(isVisible())
			refresh();
	}
	
	public void init(List<T> items)
	{
		this.items = items;

		if(isVisible())
			refresh();
	}
	
	
	
	public Chart3D initChart()
	{
		var chart = createNewChart();
 		chart.setTitleAnchor(Anchor2D.TOP_CENTER);
		chart.setLegendPosition(LegendAnchor.BOTTOM_CENTER,Orientation.HORIZONTAL);
		
		return chart;
	}
	
	protected abstract Chart3D createNewChart();

	public abstract void refresh();
	

}
