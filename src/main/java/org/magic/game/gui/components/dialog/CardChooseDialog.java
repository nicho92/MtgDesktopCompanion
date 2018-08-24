package org.magic.game.gui.components.dialog;

import java.awt.BorderLayout;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JPanel;

import org.magic.game.gui.components.DisplayableCard;
import org.magic.game.gui.components.GamePanelGUI;

public class CardChooseDialog extends JDialog {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	JComboBox<DisplayableCard> comboBox;
	DisplayableCard selected;

	public CardChooseDialog() {

		setTitle("Choose card");
		setModal(true);
		setLocationRelativeTo(null);

		getContentPane().setLayout(new BorderLayout(0, 0));

		comboBox = new JComboBox<>();

		for (DisplayableCard c : GamePanelGUI.getInstance().getPanelBattleField().getCards())
			comboBox.addItem(c);

		getContentPane().add(comboBox, BorderLayout.CENTER);

		JPanel panel = new JPanel();
		getContentPane().add(panel, BorderLayout.SOUTH);

		JButton btnOK = new JButton("Select");
		panel.add(btnOK);

		JButton btnCancel = new JButton("Cancel");
		panel.add(btnCancel);

		pack();

		btnOK.addActionListener(e -> {
			selected = (DisplayableCard) comboBox.getSelectedItem();
			dispose();
		});

		btnCancel.addActionListener(e -> {
			selected = null;
			dispose();
		});

	}

	public DisplayableCard getSelected() {
		return selected;
	}

}
