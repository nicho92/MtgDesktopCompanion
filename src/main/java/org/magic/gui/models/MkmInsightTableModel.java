package org.magic.gui.models;

import org.api.mkm.modele.InsightElement;
import org.magic.gui.abstracts.GenericTableModel;

public class MkmInsightTableModel extends GenericTableModel<InsightElement> {

	private static final long serialVersionUID = 1L;
	
	
	@Override
	public Object getValueAt(int row, int column) {
		return items.get(row);
	}
	
	
}
