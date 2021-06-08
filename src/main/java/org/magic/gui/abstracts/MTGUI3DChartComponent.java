package org.magic.gui.abstracts;

import java.awt.BorderLayout;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.jfree.chart3d.Chart3D;
import org.jfree.chart3d.Chart3DChangeEvent;
import org.jfree.chart3d.Chart3DPanel;
import org.jfree.chart3d.graphics3d.swing.DisplayPanel3D;
import org.magic.services.MTGDeckManager;

public abstract class MTGUI3DChartComponent<T> extends MTGUIComponent {


	private static final long serialVersionUID = 1L;
	protected transient List<T> items;
	protected transient MTGDeckManager manager;
	protected Chart3DPanel chartPanel;
	protected DisplayPanel3D panel;
	
	protected MTGUI3DChartComponent() {
		onlyOneRefresh=false;
		init();
	}
	
	
	public void init() {
		items = new ArrayList<>();
		manager = new MTGDeckManager();
		setLayout(new BorderLayout());
		chartPanel = new Chart3DPanel(initChart());
		panel = new DisplayPanel3D(chartPanel);
		add(panel, BorderLayout.CENTER);
		
		
		
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
	
	public abstract Chart3D initChart();
	
	public void refresh()
	{
		if(chartPanel!=null)
		{
			panel.remove(chartPanel);
		}
		
		chartPanel = new Chart3DPanel(initChart());
		
		panel.add(chartPanel, BorderLayout.CENTER);
		
		
	}


}
