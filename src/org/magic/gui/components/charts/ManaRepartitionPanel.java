package org.magic.gui.components.charts;

import java.awt.BorderLayout;
import java.awt.Color;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;

import javax.swing.JPanel;

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

public class ManaRepartitionPanel extends JPanel{

	private List<MagicCard> cards;
	ChartPanel pane;

	public ManaRepartitionPanel() {
		setLayout(new BorderLayout(0, 0));
	}
	
	public void init(MagicDeck deck) {
		cards = new ArrayList<MagicCard>();
		try{
			for(Entry<MagicCard, Integer> cci : deck.getMap().entrySet())
			{
				MagicCard mc = cci.getKey();
				for(int i=0;i<cci.getValue();i++)
					cards.add(mc);
			}
		}catch(Exception e)
		{
			
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
		for(MagicCard mc : cards)
		{
			if(mc.getColors().size()>0)
			{
				if(mc.getColors().size()==1 )
					dataset.setValue(mc.getColors().get(0), count(mc.getColors().get(0)));
				
				if(mc.getColors().size()>1 )
					dataset.setValue("Multi", count("Multi"));
					
			}
			else
			{
				dataset.setValue("Uncolor", count("Uncolor"));
			}
		}
		

        return dataset;
	}


	private Double count(String string) {
		double count=0;
		
		if(string.equals("Uncolor"))
		{	for(MagicCard mc : cards)
				if(mc.getColors().size()==0)
					count ++;
		
			return count;
		}
		else if(string.equals("Multi"))
		{	for(MagicCard mc : cards)
				if(mc.getColors().size()>1)
					count ++;
		
			return count;
		}
		else
		{
			for(MagicCard mc : cards)
				if(mc.getColors().size()==1)
					if(mc.getColors().get(0).equals(string))
						count++;
			
			return count;
		}
				
	}
	
	
	
}
	
