package org.magic.gui.components.dialog;

import java.awt.BorderLayout;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JDialog;

import org.magic.api.beans.MagicCard;
import org.magic.gui.components.CardSearchPanel;
import org.magic.services.MTGConstants;

public class CardSearchImportDialog extends JDialog {

	JButton selectCard;
	CardSearchPanel panel;

	public CardSearchImportDialog() {
		getContentPane().setLayout(new BorderLayout(0, 0));
		setModal(true);
		selectCard = new JButton(MTGConstants.ICON_IMPORT);
		panel = new CardSearchPanel();
		selectCard.addActionListener(e -> dispose());

		getContentPane().add(panel, BorderLayout.CENTER);
		getContentPane().add(selectCard, BorderLayout.SOUTH);

		setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
		pack();
	}

	public MagicCard getSelected() {
		return panel.getSelected();
	}

	public List<MagicCard> getSelection() {
		return panel.getMultiSelection();
	}

}
