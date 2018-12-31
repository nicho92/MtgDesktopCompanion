package org.magic.gui.abstracts;

import java.awt.BorderLayout;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.util.ArrayList;
import java.util.List;

import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.magic.services.MTGDeckManager;

public abstract class MTGUIChartComponent<T> extends MTGUIComponent {


	private static final long serialVersionUID = 1L;
	protected transient List<T> items;
	protected transient MTGDeckManager manager;
	protected JFreeChart chart;
	protected ChartPanel chartPanel;
	
	
	public MTGUIChartComponent() {
		init();
	}
	
	
	public void init() {
		items = new ArrayList<>();
		manager = new MTGDeckManager();
		setLayout(new BorderLayout());
		addComponentListener(new ComponentAdapter() {
			@Override
			public void componentShown(ComponentEvent componentEvent) {
				init(items);
			}

		});
	}
	
	
	public void init(List<T> items)
	{
		this.items = items;
		if(isVisible() && items!=null)
			refresh();
	}
	
	public abstract void drawGraph();
	
	public void refresh()
	{
		removeAll();
		
		if(items==null)
			return;
		
		drawGraph();
		
		if(chart!=null)
			chart.fireChartChanged();
		
		if(chartPanel!=null)
			chartPanel.revalidate();
	}


}
