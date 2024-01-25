package org.magic.gui.components.dialog;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.WindowConstants;

import org.magic.api.beans.MTGCard;
import org.magic.gui.components.card.CardSearchPanel;
import org.magic.services.MTGConstants;

public class CardSearchImportDialog extends JDialog {

	/**
	 *
	 */
	private static final long serialVersionUID = 1L;
	JButton selectCard;
	CardSearchPanel panel;

	public CardSearchImportDialog() {
		getContentPane().setLayout(new BorderLayout(0, 0));
		setModal(true);
		setIconImage(MTGConstants.ICON_SEARCH.getImage());
		selectCard = new JButton(MTGConstants.ICON_IMPORT);
		panel = new CardSearchPanel();
		panel.setPreferredSize(new Dimension(900, 700));
	
		selectCard.addActionListener(e -> dispose());

		getContentPane().add(panel, BorderLayout.CENTER);
		getContentPane().add(selectCard, BorderLayout.SOUTH);

		setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
		pack();
	}

	public MTGCard getSelected() {
		return panel.getSelected();
	}

	public List<MTGCard> getSelection() {
		return panel.getMultiSelection();
	}

}
