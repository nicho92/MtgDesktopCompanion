package org.magic.game.gui.components;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

import javax.swing.JComboBox;
import javax.swing.JDialog;

public class DisplayableCardsChooser extends JDialog {

	private DisplayableCard selectedCard;
	
	public DisplayableCardsChooser(List<DisplayableCard> cards) {
		setTitle("Choose Cards");
		
		final JComboBox comboBox = new JComboBox(cards.toArray());
		comboBox.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				selectedCard = (DisplayableCard)comboBox.getSelectedItem();
				dispose();
			}
		});
		getContentPane().add(comboBox, BorderLayout.CENTER);
		setModal(true);
		pack();
	}
	
	public DisplayableCard getSelectedCard() {
		return selectedCard;
	}
}
