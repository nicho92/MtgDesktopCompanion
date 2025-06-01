package org.magic.gui.components.dialog.importer;

import java.awt.Dimension;
import java.util.List;

import javax.swing.JComponent;

import org.magic.api.beans.MTGCard;
import org.magic.gui.abstracts.AbstractDelegatedImporterDialog;
import org.magic.gui.components.card.CardSearchPanel;

public class CardImporterDialog extends AbstractDelegatedImporterDialog<MTGCard> {

	private static final long serialVersionUID = 1L;
	private CardSearchPanel panel;

	@Override
	public MTGCard getSelectedItem() {
		return panel.getSelected();
	}
	
	
	@Override
	public List<MTGCard> getSelectedItems() {
		return panel.getMultiSelection();
	}
	
	@Override
	public JComponent getSelectComponent() {
		panel = new CardSearchPanel();
		panel.getContextTabbedPane().setVisible(false);
		panel.setPreferredSize(new Dimension(900, 700));
		return panel;
	}

}
