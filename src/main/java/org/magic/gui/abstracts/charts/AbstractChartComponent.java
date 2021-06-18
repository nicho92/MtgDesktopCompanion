package org.magic.gui.abstracts.charts;

import java.awt.BorderLayout;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.magic.gui.abstracts.MTGUIComponent;
import org.magic.services.MTGDeckManager;

public abstract class AbstractChartComponent<T> extends MTGUIComponent {

	private static final long serialVersionUID = 1L;
	protected transient List<T> items;

	protected abstract void createNewChart();
	public abstract void refresh();
	protected transient MTGDeckManager manager;

	
	protected AbstractChartComponent() {
		items = new ArrayList<>();
		
		manager = new MTGDeckManager();
		setLayout(new BorderLayout());
		
	}
	
	
	public boolean showLegend()
	{
		return true;
	}

	
	public boolean showLabel()
	{
		return true;
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
	
	
}
