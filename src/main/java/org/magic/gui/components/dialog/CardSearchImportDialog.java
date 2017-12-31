package org.magic.gui.components.dialog;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

import org.magic.api.beans.MagicCard;
import org.magic.api.interfaces.MagicCardsProvider;
import org.magic.gui.components.CardSearchPanel;
import org.magic.services.MTGConstants;

public class CardSearchImportDialog extends JDialog {
	
	JButton selectCard;
	CardSearchPanel cardSearchPanel;
	
	public CardSearchImportDialog() {
		getContentPane().setLayout(new BorderLayout(0, 0));
		setModal(true);
		selectCard = new JButton(MTGConstants.ICON_IMPORT);
		cardSearchPanel = new CardSearchPanel();
		
		
		selectCard.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				dispose();
			}
		});
		
		getContentPane().add(cardSearchPanel, BorderLayout.CENTER);
		getContentPane().add(selectCard, BorderLayout.SOUTH);
		
		setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
		pack();
	}
	
	public MagicCard getSelected()
	{
		return cardSearchPanel.getSelected();
	}
	
	
	public List<MagicCard> getSelection()
	{
		return cardSearchPanel.getMultiSelection();
	}
	
}
