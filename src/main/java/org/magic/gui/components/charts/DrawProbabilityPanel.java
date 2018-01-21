package org.magic.gui.components.charts;

import java.awt.BorderLayout;

import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.table.AbstractTableModel;

import org.jdesktop.swingx.JXTable;
import org.magic.api.beans.MagicCard;
import org.magic.api.beans.MagicDeck;
import org.magic.services.MTGControler;
import org.magic.tools.DeckCalculator;
import javax.swing.JLabel;
import java.awt.Color;
import java.awt.Font;


public class DrawProbabilityPanel extends JPanel {
	
	private JXTable table;
	private DeckCalculator calc;
	private AbstractTableModel model;
	int maxTurn=10;
	
	
	public DrawProbabilityPanel() {
		initGUI();
	}
	
	public void setMaxTurn(int maxTurn) {
		this.maxTurn = maxTurn;
	}
	
	private void initGUI() {
		setLayout(new BorderLayout(0, 0));
		JScrollPane scrollPane = new JScrollPane();
		add(scrollPane, BorderLayout.CENTER);
		table = new JXTable();
		scrollPane.setViewportView(table);
		
		JPanel panel = new JPanel();
		panel.setBackground(Color.WHITE);
		add(panel, BorderLayout.NORTH);
		
		JLabel lblDrawProbability = new JLabel(MTGControler.getInstance().getLangService().getCapitalize("DRAW_PROBABILITIES"));
			   lblDrawProbability.setFont(new Font("Tahoma", Font.BOLD, 14));
		panel.add(lblDrawProbability);
	}
	
	public void init(MagicDeck d)
	{
		calc=new DeckCalculator(d);
		initDeck();
	}

	public void init(MagicDeck d,MagicCard c)
	{
		calc=new DeckCalculator(d);
		
		if(c!=null)
			initCard(c);
	}

	
	private void initCard(MagicCard card)
	{
		model = new AbstractTableModel() {
			
			
			@Override
			public String getColumnName(int t) {
				
				if(t==0)
					return "Turn";
				
				return card.getName();
			}
			
			@Override
			public Object getValueAt(int r, int c) {
				if(c==0)
				{
					return "Turn " + (r);
				}
				else
				{
					return calc.format(calc.getProbability(r, card));
				}
			}
			
			@Override
			public int getRowCount() {
				return maxTurn+1;
			}
			
			@Override
			public int getColumnCount() {
				return 2;
			}
		};
	
		table.setModel(model);
		model.fireTableDataChanged();
		table.packAll();
	}
	
	private void initDeck()
	{
		model = new AbstractTableModel() {
			
			@Override
			public String getColumnName(int t) {
				if(t==0)
					return "Card";
				else
					return "Turn "+t;
			}
			
			@Override
			public Object getValueAt(int r, int c) {
				if(c==0)
				{
					return calc.getUniqueCards().get(r);
				}
				else
				{
					return calc.format(calc.getProbability(c-1, calc.getUniqueCards().get(r)));
				}
			}
			
			@Override
			public int getRowCount() {
				return calc.getDeck().getMap().keySet().size();
			}
			
			@Override
			public int getColumnCount() {
				return maxTurn+1;
			}
		};
	
		table.setModel(model);
		model.fireTableDataChanged();
		table.packAll();
	}
	
	
}
