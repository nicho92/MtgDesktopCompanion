package org.magic.gui.components.charts;

import java.awt.BorderLayout;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.util.Date;
import java.util.List;

import javax.swing.JPanel;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.data.time.Month;
import org.jfree.data.time.TimeSeries;
import org.jfree.data.time.TimeSeriesCollection;
import org.magic.api.beans.OrderEntry;
import org.magic.api.beans.OrderEntry.TYPE_TRANSACTION;
import org.magic.api.interfaces.MTGDao;
import org.magic.services.MTGControler;

public class FinancialChartPanel extends JPanel {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private ChartPanel pane;
	private String title ="Financial History";
	
	public FinancialChartPanel() {
		setLayout(new BorderLayout(0, 0));
		pane = new ChartPanel(null);
		
		addComponentListener(new ComponentAdapter() {
			@Override
			public void componentShown(ComponentEvent componentEvent) {
				init();
			}
		});
	}
	
	public void init() {
		if(isVisible()) {
			refresh();
		}
	}

	private void refresh() {
		TimeSeriesCollection dataset = new TimeSeriesCollection();

		TimeSeries series = new TimeSeries(TYPE_TRANSACTION.BUY.name());
		TimeSeries seriesS = new TimeSeries(TYPE_TRANSACTION.SELL.name());
		
		
		for (Date d : MTGControler.getInstance().getEnabled(MTGDao.class).listDatesOrders()) 
		{
			List<OrderEntry> list = MTGControler.getInstance().getEnabled(MTGDao.class).listOrdersAt(d);
			list.forEach(o->{
				if(o.getTypeTransaction().equals(TYPE_TRANSACTION.BUY))
					series.addOrUpdate(new Month(d),o.getItemPrice());
				else
					seriesS.addOrUpdate(new Month(d),o.getItemPrice());
				
			});
			
		}
		dataset.addSeries(series);
		dataset.addSeries(seriesS);
		
		
		JFreeChart chart = ChartFactory.createTimeSeriesChart(title, "Date", "Price", dataset, true, true,false);

		pane.setChart(chart);
		pane.addMouseWheelListener(mwe -> {
			if (mwe.getWheelRotation() > 0) {
				pane.zoomOutDomain(0.5, 0.5);

			} else if (mwe.getWheelRotation() < 0) {
				pane.zoomInDomain(1.5, 1.5);
			}
		});
		this.add(pane, BorderLayout.CENTER);
		chart.fireChartChanged();
	}

	public void zoom() {
		// do nothing
	}

}
