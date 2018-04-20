package org.magic.gui.components.charts;

import java.awt.BorderLayout;
import java.awt.Color;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;

import javax.swing.JPanel;

import org.apache.log4j.Logger;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.labels.PieSectionLabelGenerator;
import org.jfree.chart.labels.StandardPieSectionLabelGenerator;
import org.jfree.chart.plot.PiePlot;
import org.jfree.data.general.DefaultPieDataset;
import org.jfree.data.general.PieDataset;
import org.magic.api.beans.MagicCard;
import org.magic.api.beans.MagicDeck;
import org.magic.services.MTGDeckManager;
import org.magic.services.MTGLogger;

public class ManaRepartitionPanel extends JPanel{

	private List<MagicCard> cards;
	private transient Logger logger = MTGLogger.getLogger(this.getClass());
	private transient MTGDeckManager manager;
	
	public ManaRepartitionPanel() {
		manager = new MTGDeckManager();
		setLayout(new BorderLayout(0, 0));
	}
	
	public void init(MagicDeck deck) {
		cards = new ArrayList<>();
		try{
			
			cards = deck.getAsList();
			
		}catch(Exception e)
		{
			logger.error("Error init " + deck,e);
		}
		refresh();
	}
	
	
	public void init(List<MagicCard> cards)
	{
		this.cards=cards;
		refresh();
	}
	
	private void refresh()
	{
		this.removeAll();
		
		JFreeChart chart = ChartFactory.createPieChart3D(
	            "Color repartition",  // chart title
	            getManaRepartitionDataSet(),             // data
	            false,               // include legend
	            true,
	            true
	        );
	
		ChartPanel pane = new ChartPanel(chart);
		PiePlot plot = (PiePlot) chart.getPlot();
		plot.setSectionPaint("Black", Color.BLACK);
		plot.setSectionPaint("White", Color.WHITE);
		plot.setSectionPaint("Blue", Color.BLUE);
		plot.setSectionPaint("Green", Color.GREEN);
		plot.setSectionPaint("Red", Color.RED);
		plot.setSectionPaint("Multi", Color.YELLOW);
		plot.setSectionPaint("Uncolor", Color.GRAY);
		plot.setSectionPaint("black", Color.BLACK);
		plot.setSectionPaint("white", Color.WHITE);
		plot.setSectionPaint("blue", Color.BLUE);
		plot.setSectionPaint("green", Color.GREEN);
		plot.setSectionPaint("red", Color.RED);
		plot.setSectionPaint("multi", Color.YELLOW);
		plot.setSectionPaint("uncolor", Color.GRAY);
		plot.setSimpleLabels(true);
		
		PieSectionLabelGenerator generator = new StandardPieSectionLabelGenerator("{1}", new DecimalFormat("0"), new DecimalFormat("0.00%"));
		plot.setLabelGenerator(generator);
		
		this.add(pane,BorderLayout.CENTER);
		this.revalidate();
		this.repaint();
		
	}
	
	private PieDataset getManaRepartitionDataSet() 
	{
		DefaultPieDataset dataset = new DefaultPieDataset();
		for(Entry<String,Integer> data : manager.analyseColors(cards).entrySet())
		{
			dataset.setValue(data.getKey(),data.getValue());
			
		}
		

        return dataset;
	}

	
	
}
	
