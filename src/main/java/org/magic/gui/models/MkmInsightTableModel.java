package org.magic.gui.models;

import org.api.mkm.modele.InsightElement;
import org.magic.gui.abstracts.GenericTableModel;

public class MkmInsightTableModel extends GenericTableModel<InsightElement> {

	private static final long serialVersionUID = 1L;
	
	
	
	public MkmInsightTableModel() {
		setColumns(new String[] {"CARD","EDITION","YESTERDAY","TODAY"});
	}
	
	@Override
	public Object getValueAt(int row, int column) {
	
		switch(column)
		{
		case 0 : return items.get(row).getCardName();
		case 1 : return items.get(row).getEd();
		case 2 : return items.get(row).getYesterdayStock();
		case 3 : return items.get(row).getStock();
		}
		
		
		return items.get(row);
	}
	
	
}
