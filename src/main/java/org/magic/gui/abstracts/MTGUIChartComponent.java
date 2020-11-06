package org.magic.gui.abstracts;

import java.awt.BorderLayout;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.magic.services.MTGDeckManager;

public abstract class MTGUIChartComponent<T> extends MTGUIComponent {


	private static final long serialVersionUID = 1L;
	protected transient List<T> items;
	protected transient MTGDeckManager manager;
	protected ChartPanel chartPanel;

	
	protected MTGUIChartComponent() {
		onlyOneRefresh=false;
		init();
	}
	
	
	public void init() {
		items = new ArrayList<>();
		manager = new MTGDeckManager();
		setLayout(new BorderLayout());
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
	
	public abstract JFreeChart initChart();
	
	public void refresh()
	{

		if(items==null)
			return;
		
		JFreeChart chart = initChart();
		chartPanel.setChart(chart);
		
		if(chart!=null)
			chart.fireChartChanged();

	}


}
