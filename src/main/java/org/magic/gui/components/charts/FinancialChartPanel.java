package org.magic.gui.components.charts;

import java.awt.BorderLayout;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;

import javax.swing.JPanel;

import org.apache.log4j.Logger;
import org.jfree.chart.ChartPanel;
import org.magic.services.MTGLogger;

public class FinancialChartPanel extends JPanel {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private transient Logger logger = MTGLogger.getLogger(this.getClass());
	private ChartPanel pane;
	private String title;
	
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
/*
		TimeSeriesCollection dataset = new TimeSeriesCollection();

		TimeSeries series1 = new TimeSeries(title);
			for (MTGDashBoard d : MTGControler.getInstance().getPlugins(MTGDashBoard.class)) 
			{
				TimeSeries series = new TimeSeries(d.getName());
				CardPriceVariations mapTime;
				try {
					mapTime = d.getPriceVariation(mc, me);
					if (mapTime != null) {
						for (Entry<Date, Double> da : mapTime.entrySet())
							series.add(new Day(da.getKey()), da.getValue().doubleValue());

						dataset.addSeries(series);
					}

				} catch (IOException e) {
					logger.error("Error refresh", e);
				}

			}

		

		JFreeChart chart = ChartFactory.createTimeSeriesChart("Price Variation", "Date", "Price", dataset, true, true,false);

		pane.setChart(chart);
		pane.addMouseWheelListener(mwe -> {
			if (mwe.getWheelRotation() > 0) {
				pane.zoomOutDomain(0.5, 0.5);

			} else if (mwe.getWheelRotation() < 0) {
				pane.zoomInDomain(1.5, 1.5);
			}
		});
		this.add(pane, BorderLayout.CENTER);
		chart.fireChartChanged();*/
	}

	public void zoom() {
		// do nothing
	}

}
