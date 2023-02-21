package org.magic.gui.components.shops;

import java.util.List;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.table.TableModel;

import org.magic.api.beans.enums.TransactionDirection;
import org.magic.api.beans.shop.Transaction;
import org.magic.services.MTGConstants;
import org.magic.services.tools.UITools;

public class TransactionTotalPanel extends JPanel {
	private JLabel totalBuy;
	private JLabel totalSell;
	private JLabel total;
	private JLabel selectionBuy;
	private JLabel selectionSell;
	private JLabel totalSelection;
	
	
	
	public TransactionTotalPanel() {
		totalBuy = new JLabel(MTGConstants.ICON_DOWN);
		totalSell = new JLabel(MTGConstants.ICON_UP);
		total = new JLabel();
		totalSelection = new JLabel();
		selectionSell = new JLabel(MTGConstants.ICON_UP);
		selectionBuy=new JLabel(MTGConstants.ICON_DOWN);
		
		
		add(totalBuy);
		add(totalSell);
		add(total);
		add(new JLabel(" ("));
		add(selectionBuy);
		add(selectionSell);
		add(totalSelection);
		add(new JLabel(")"));
	}
	
	public void calulate(List<Transaction> entries,TableModel model)
	{
		double totalS=0;
		double totalB=0;
		
		for(Transaction e : entries)
		{
			if(e.getTypeTransaction() ==TransactionDirection.BUY)
				totalB+=e.total();
			else
				totalS+=e.total();
		}
		
		if(entries.size()<model.getRowCount())
		{
			selectionBuy.setText(UITools.formatDouble(totalB));
			selectionSell.setText(UITools.formatDouble(totalS));
			totalSelection.setText(": "+UITools.formatDouble(totalS-totalB)+")");
			if((totalS-totalB)>0)
				totalSelection.setIcon(MTGConstants.ICON_UP);
			else
				totalSelection.setIcon(MTGConstants.ICON_DOWN);

		}
		else
		{
			totalBuy.setText(UITools.formatDouble(totalB));
			totalSell.setText(UITools.formatDouble(totalS));
			total.setText(": "+UITools.formatDouble(totalS-totalB));

			if((totalS-totalB)>0)
				total.setIcon(MTGConstants.ICON_UP);
			else
				total.setIcon(MTGConstants.ICON_DOWN);
		}
	}

	
}
