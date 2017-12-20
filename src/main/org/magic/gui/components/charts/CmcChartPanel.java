package org.magic.gui.components.charts;

import java.awt.BorderLayout;
import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;
import java.util.TreeMap;

import javax.swing.JPanel;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.category.CategoryDataset;
import org.jfree.data.category.DefaultCategoryDataset;
import org.magic.api.beans.MagicCard;
import org.magic.api.beans.MagicDeck;


public class CmcChartPanel extends JPanel{

	private List<MagicCard> cards;



	public CmcChartPanel() {
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
	
	
	
	ChartPanel pane;
	
	private void refresh()
	{
		this.removeAll();
		
		JFreeChart chart = ChartFactory.createBarChart(
                "Mana Curve",
                "cost",
                "number",
                getManaCurveDataSet(),
                PlotOrientation.VERTICAL,
                true,
                true,
                false
                );
	
		
		pane = new ChartPanel(chart);
		
		this.add(pane,BorderLayout.CENTER);
		chart.fireChartChanged();
	}

	public void init(List<MagicCard> cards)
	{
		this.cards=cards;
		refresh();
	}
	


	private CategoryDataset  getManaCurveDataSet() 
	{
		DefaultCategoryDataset dataset = new DefaultCategoryDataset();
		TreeMap<Integer, Number> temp = new TreeMap<Integer, Number>();
		
			for(MagicCard mc : cards)
			{
				if(mc.getCmc()!=null)
					if(!mc.getTypes().contains("Land"))
						temp.put(mc.getCmc(),count(mc.getCmc()) );
				
//					if(mc.getCmc()==null)
//						temp.put(0,count(0) );
//					else
//						temp.put(mc.getCmc(),count(mc.getCmc()) );
				
			}
			for(Integer k : temp.keySet())
				dataset.addValue(temp.get(k), "cmc",k);
				
		
        return dataset;
	}


	private Integer count(Integer cmc) {
		int count=0;
		
		for(MagicCard mc : cards)
		{
			if(!mc.getTypes().contains("Land"))
			{
				int cm = (mc.getCmc()==null)? 0 : mc.getCmc();
				if(cm==cmc)
					count++;
			}
		}
		return count;
				
	}


	
	
	
}



