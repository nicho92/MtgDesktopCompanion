package org.magic.gui.components.charts;

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

public class TypeRepartitionPanel extends JPanel{

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
	
	
	private void refresh()
	{
		this.removeAll();
		
		JFreeChart chart = ChartFactory.createPieChart3D(
	            "Type repartition",  // chart title
	            getTypeRepartitionDataSet(),             // data
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
		
		  PieSectionLabelGenerator generator = new StandardPieSectionLabelGenerator("{0} = {1}", new DecimalFormat("0"), new DecimalFormat("0.00%"));
		    plot.setLabelGenerator(generator);
		this.add(pane);
	}
	
	public void init(List<MagicCard> cards)
	{
		this.cards=cards;
		refresh();
	}
	private PieDataset getTypeRepartitionDataSet() 
	{
		DefaultPieDataset dataset = new DefaultPieDataset();
		for(MagicCard mc : cards)
		{
			if(mc.getTypes()!=null)
				dataset.setValue(mc.getTypes().get(0), count(mc.getTypes().get(0)));
		}


        return dataset;
	}


	private Double count(String string) {
		double count=0;
		for(MagicCard mc : cards)
				if(mc.getTypes().get(0)!=null)
					if(mc.getTypes().get(0).equals(string))
						count ++;
		return count;
	}
	
	
	
}
	
