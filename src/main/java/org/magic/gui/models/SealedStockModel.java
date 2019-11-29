package org.magic.gui.models;

import org.magic.api.beans.SealedStock;
import org.magic.gui.abstracts.GenericTableModel;

public class SealedStockModel extends GenericTableModel<SealedStock> {

	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public SealedStockModel() {
		setColumns("ID","Type","Edition","Qty");
	}
	
}
