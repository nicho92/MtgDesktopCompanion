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
	protected ChartPanel chartPanel;

	
	public MTGUIChartComponent() {
		init();
	}
	
	
	public void init() {
		items = new ArrayList<>();
		manager = new MTGDeckManager();
		setLayout(new BorderLayout());
		chartPanel = new ChartPanel(null,true);
		add(chartPanel, BorderLayout.CENTER);

		
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
	
		if(isVisible())
			refresh();
	}
	
	public abstract JFreeChart initChart();
	
	public void refresh()
	{
		if(items==null)
			return;
		
		
		JFreeChart chart = initChart();
		chartPanel.setChart(chart);
		
		if(chart!=null)
			chart.fireChartChanged();
		
		chartPanel.revalidate();
	}


}
