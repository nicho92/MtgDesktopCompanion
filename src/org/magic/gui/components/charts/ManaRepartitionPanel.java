package org.magic.gui.components.charts;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;

import javax.swing.JPanel;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PiePlot;
import org.jfree.data.general.DefaultPieDataset;
import org.jfree.data.general.PieDataset;
import org.magic.api.beans.MagicCard;
import org.magic.api.beans.MagicDeck;

public class ManaRepartitionPanel extends JPanel{

	private List<MagicCard> cards;
	ChartPanel pane;

	public void init(MagicDeck deck) {
		cards = new ArrayList<MagicCard>();
		for(Entry<MagicCard, Integer> cci : deck.getMap().entrySet())
		{
			MagicCard mc = cci.getKey();
			for(int i=0;i<cci.getValue();i++)
				cards.add(mc);
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
	
		pane = new ChartPanel(chart);
		PiePlot plot = (PiePlot) chart.getPlot();
		plot.setSectionPaint("B", Color.BLACK);
		plot.setSectionPaint("W", Color.WHITE);
		plot.setSectionPaint("U", Color.BLUE);
		plot.setSectionPaint("G", Color.GREEN);
		plot.setSectionPaint("R", Color.RED);
		plot.setSectionPaint("multi", Color.YELLOW);
		plot.setSectionPaint("uncolor", Color.GRAY);
		
		
		this.add(pane);
	}
	
	private PieDataset getManaRepartitionDataSet() 
	{
		DefaultPieDataset dataset = new DefaultPieDataset();
		for(MagicCard mc : cards)
		{
			if(mc.getColorIdentity().size()==1 )
				dataset.setValue(mc.getColorIdentity().get(0), count(mc.getColorIdentity().get(0)));
			
			if(mc.getColorIdentity().size()>1 )
				dataset.setValue("multi", count("multi"));
			
			if(mc.getColorIdentity().size()<1 )
				dataset.setValue("uncolor", count("uncolor"));
				
		}


        return dataset;
	}


	private Double count(String string) {
		double count=0;
		
		if(string.equals("uncolor"))
		{	for(MagicCard mc : cards)
				if(mc.getColorIdentity().size()<1)
					count ++;
		
			return count;
		}
		else if(string.equals("multi"))
		{	for(MagicCard mc : cards)
				if(mc.getColorIdentity().size()>1)
					count ++;
		
			return count;
		}
		else
		{
			for(MagicCard mc : cards)
				if(mc.getColorIdentity().size()==1)
					if(mc.getColorIdentity().get(0).equals(string))
						count++;
			
			return count;
		}
				
	}
	
	
	
}
	
