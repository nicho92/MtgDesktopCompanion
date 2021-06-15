package org.magic.gui.components.charts;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.beanutils.PropertyUtils;
import org.jfree.chart3d.Chart3D;
import org.jfree.chart3d.Chart3DFactory;
import org.jfree.chart3d.Colors;
import org.jfree.chart3d.Orientation;
import org.jfree.chart3d.data.PieDataset3D;
import org.jfree.chart3d.data.StandardPieDataset3D;
import org.jfree.chart3d.graphics2d.Anchor2D;
import org.jfree.chart3d.plot.PiePlot3D;
import org.jfree.chart3d.plot.Plot3D;
import org.jfree.chart3d.style.ChartStyle;
import org.jfree.chart3d.table.TextElement;
import org.magic.api.beans.OrderEntry;
import org.magic.api.beans.Transaction;
import org.magic.gui.abstracts.MTGUI3DChartComponent;
import org.magic.tools.UITools;
	

public class TransactionChartPanel extends MTGUI3DChartComponent<Transaction> {

	private static final long serialVersionUID = 1L;
	private String property;
	private boolean count;
	private Chart3D chart;
	

	@Override
	protected Chart3D createNewChart() {
		chart=null ;
				chart=Chart3DFactory.createPieChart(
		                getTitle(), 
		                "", 
		                getDataSet());
				
				chart.setLegendBuilder((Plot3D plot, Anchor2D anchor, Orientation orientation, ChartStyle style)->new TextElement(""));
		return chart;
	}


	@Override
	public void refresh() {
		PiePlot3D plot = (PiePlot3D) chart.getPlot();
        plot.setDataset(getDataSet());
    	plot.setSectionColors(Colors.createPastelColors());
    	plot.setSectionLabelGenerator(( PieDataset3D dataset, Comparable<?> key)->String.valueOf(key));
    	plot.setToolTipGenerator((PieDataset3D dataset, Comparable<?> key)->String.valueOf(key + ":" + dataset.getValue(key)));
    	chartPanel.zoomToFit();
	}


	private PieDataset3D<String> getDataSet() {
		var dataset = new StandardPieDataset3D<String>();
		
		for (Entry<Object, Double> data : groupOrdersBy().entrySet()) {
			dataset.add(String.valueOf(data.getKey()), data.getValue());
		}
		return dataset;
	}
	

	@Override
	public String getTitle() {
		return "Transactions";
	}


	public void init(List<Transaction> listOrders, String p, boolean count) {
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
					ret.put(val, ret.get(val)==null? o.total() : UITools.roundDouble(ret.get(val)+o.total()));
			

			} catch (Exception e) {
				logger.error(e);
			} 
			
		});
		return ret;
	}



}
