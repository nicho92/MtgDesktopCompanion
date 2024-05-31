package org.magic.gui.components.dialog.importer;

import java.util.List;

import javax.swing.JComponent;

import org.magic.api.beans.MTGCardStock;
import org.magic.gui.abstracts.AbstractDelegatedImporterDialog;
import org.magic.gui.components.card.CardStockPanel;

public class CardStockImportDialog extends AbstractDelegatedImporterDialog<MTGCardStock> {

	private static final long serialVersionUID = 1L;
	CardStockPanel panel;

	
	
	@Override
	public MTGCardStock getSelectedItem() {
		return panel.getSelected();
	}
	
	@Override
	public List<MTGCardStock> getSelectedItems() {
		return panel.getMultiSelection();
	}
	
	@Override
	public JComponent getSelectComponent() {
		panel = new CardStockPanel();
		
		return panel;
	}

}
