package org.magic.gui.components.charts;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.jfree.chart3d.data.PieDataset3D;
import org.jfree.chart3d.data.StandardPieDataset3D;
import org.magic.api.interfaces.MTGStockItem;
import org.magic.gui.abstracts.charts.Abstract3DPieChart;
import org.magic.services.tools.BeanTools;
import org.magic.services.tools.UITools;


public class MTGStockItemsChartPanel extends Abstract3DPieChart<MTGStockItem,String> {

	public MTGStockItemsChartPanel(boolean displayPanel) {
		super(displayPanel);
	}

	private static final long serialVersionUID = 1L;
	private String property;
	private boolean count;


	@Override
	public boolean showLegend() {
		return false;
	}

	@Override
	public PieDataset3D<String> getDataSet() {
		var dataset = new StandardPieDataset3D<String>();

		for (Entry<Object, Double> data : groupOrdersBy().entrySet()) {
			
			var k = data.getKey();
			
			dataset.add(""+k, data.getValue());
		}
		return dataset;
	}


	@Override
	public String getTitle() {
		return "Transactions";
	}

	public void init(List<MTGStockItem> listOrders, String p, boolean count) {
		this.property=p;
		this.count=count;
		init(listOrders);
	}
	
	
	

	private Map<Object, Double> groupOrdersBy() {

		Map<Object, Double> ret = new HashMap<>();

		items.forEach(o->{
			try
			{
				Object val = BeanTools.readProperty(o, property);
			
				if(count)
					ret.put(val, ret.get(val)==null? 1 : ret.get(val)+1);
				else
					ret.put(val, ret.get(val)==null? UITools.roundDouble(o.getQte()*o.getPrice()) : UITools.roundDouble(ret.get(val)+(o.getQte()*o.getPrice())));


			} catch (Exception e) {
				logger.error(e);
			}

		});
		return ret;
	}



}
