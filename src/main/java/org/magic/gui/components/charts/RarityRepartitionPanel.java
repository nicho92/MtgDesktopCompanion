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

public class RarityRepartitionPanel extends JPanel{

	private List<MagicCard> cards;
	ChartPanel pane;
	
	public RarityRepartitionPanel() {
		setLayout(new BorderLayout(0, 0));
	}

	public void init(MagicDeck deck) {
		cards = new ArrayList<MagicCard>();
		if(deck!=null)
			if(deck.getMap()!=null)
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
	            "Rarity repartition",  // chart title
	            getRarityRepartitionDataSet(),             // data
	            false,               // include legend
	            true,
	            true
	        );
	
		pane = new ChartPanel(chart);
		PiePlot plot = (PiePlot) chart.getPlot();
		plot.setSectionPaint("Uncommon", Color.GRAY);
		plot.setSectionPaint("Common", Color.WHITE);
		plot.setSectionPaint("Rare", Color.YELLOW);
		plot.setSectionPaint("Mythic Rare", Color.ORANGE);
		
		 PieSectionLabelGenerator generator = new StandardPieSectionLabelGenerator("{0} = {1}", new DecimalFormat("0"), new DecimalFormat("0.00%"));
		    plot.setLabelGenerator(generator);
		this.add(pane,BorderLayout.CENTER);
	}
	
	private PieDataset getRarityRepartitionDataSet() 
	{
		DefaultPieDataset dataset = new DefaultPieDataset();
		for(MagicCard mc : cards)
		{
			dataset.setValue(mc.getEditions().get(0).getRarity(), count(mc.getEditions().get(0).getRarity()));
		}


        return dataset;
	}


	private Double count(String string) {
		double count=0;
		
				for(MagicCard mc : cards)
				{	
					try{
						if(mc.getEditions().get(0).getRarity().equals(string))
							count++;
						
					}catch (Exception e) {
						System.err.println(mc + " "+ e.getMessage());
					}
				}
			
			return count;
			
				
	}
	
	
	
}
	
