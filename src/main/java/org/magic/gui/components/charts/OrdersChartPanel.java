package org.magic.gui.components.charts;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.beanutils.PropertyUtils;
import org.jfree.chart3d.data.PieDataset3D;
import org.jfree.chart3d.data.StandardPieDataset3D;
import org.magic.api.beans.OrderEntry;
import org.magic.gui.abstracts.charts.Abstract3DPieChart;
import org.magic.tools.UITools;
	

public class OrdersChartPanel extends Abstract3DPieChart<OrderEntry,String> {

	public OrdersChartPanel(boolean displayPanel) {
		super(displayPanel);
	}

	private static final long serialVersionUID = 1L;
	private String property;
	private boolean count;

	

	public PieDataset3D<String> getDataSet() {
		var dataset = new StandardPieDataset3D<String>();
		
		for (Entry<Object, Double> data : groupOrdersBy().entrySet()) {
			dataset.add(String.valueOf(data.getKey()), data.getValue());
		}
		return dataset;
	}
	
	
	@Override
	public String getTitle() {
		return "Orders";
	}


	public void init(List<OrderEntry> listOrders, String p, boolean count) {
		this.property=p;
		this.count=count;
		init(listOrders);
	}
	
	private Map<Object, Double> groupOrdersBy() {
		
		Map<Object, Double> ret = new HashMap<>();
		
		items.forEach(o->{
			try 
			{
				Object val = PropertyUtils.getProperty(o, property);
				if(count)
					ret.put(val, ret.get(val)==null? 1 : ret.get(val)+1);
				else
					ret.put(val, ret.get(val)==null? o.getItemPrice() : UITools.roundDouble(ret.get(val)+o.getItemPrice()));

			} catch (Exception e) {
				logger.error(e);
			} 
			
		});
		return ret;
	}



}
