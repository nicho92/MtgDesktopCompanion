package org.magic.gui.models;

import org.api.mkm.modele.InsightElement;
import org.magic.gui.abstracts.GenericTableModel;
import org.magic.tools.UITools;

public class MkmInsightTableModel extends GenericTableModel<InsightElement> {

	private static final long serialVersionUID = 1L;
	
	
	
	public MkmInsightTableModel() {
		setColumns(new String[] {"CARD","EDITION","YESTERDAY","TODAY","TOTAL"});
	}
	
	@Override
	public Object getValueAt(int row, int column) {
		InsightElement it = items.get(row);
		switch(column)
		{
			case 0 : return it.getCardName();
			case 1 : return it.getEd();
			case 2 : return it.getYesterdayStock();
			case 3 : return it.getStock();
			case 4 : return it.getChangeValue();
			default : return it;
		}
	}
		
}
